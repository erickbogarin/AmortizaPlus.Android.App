package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.Installment
import com.elab.amortizaplus.domain.util.MathUtils.roundTwo
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.pow

/**
 * Calculadora para o sistema PRICE (Tabela Francesa)
 *
 * CORREÇÕES APLICADAS (v4 - FINAL):
 * - Epsilon separado para cálculos matemáticos (1e-10) vs saldos (0.01)
 * - Amortização sempre limitada ao saldo disponível
 * - Loop termina quando saldo < 0.01
 * - Proteções robustas contra valores inválidos
 */
class PriceCalculator {

    companion object {
        // Epsilon para saldos monetários
        private const val BALANCE_EPSILON = 0.01

        // Epsilon para cálculos matemáticos precisos
        private const val MATH_EPSILON = 1e-10
    }

    fun calculate(
        loanAmount: Double,
        monthlyRate: Double,
        terms: Int,
        extraAmortizations: Map<Int, ExtraAmortizationInput> = emptyMap()
    ): List<Installment> {
        val installments = mutableListOf<Installment>()
        var remainingBalance = loanAmount
        var payment = computePayment(remainingBalance, monthlyRate, terms)
        var effectiveTerms = terms
        var month = 1

        // Loop principal - termina quando saldo < 0.01
        while (month <= effectiveTerms && remainingBalance >= BALANCE_EPSILON) {

            val extraInput = extraAmortizations[month]
            val requestedExtra = extraInput?.amount ?: 0.0
            val shouldReduceTerm = extraInput?.reduceTerm ?: true

            // Cálculo dos componentes da parcela
            val rawInterest = remainingBalance * monthlyRate
            var rawAmortization = payment - rawInterest

            // Garante que amortização não seja negativa
            rawAmortization = rawAmortization.coerceAtLeast(0.0)

            // CRÍTICO: amortização nunca pode exceder o saldo
            rawAmortization = min(rawAmortization, remainingBalance)

            // Aplica amortização básica
            var balanceAfterAmortization = remainingBalance - rawAmortization

            // Aplica amortização extra
            val extraRaw = min(requestedExtra, balanceAfterAmortization)
            balanceAfterAmortization -= extraRaw

            // Garante que não fique negativo
            balanceAfterAmortization = balanceAfterAmortization.coerceAtLeast(0.0)

            // Arredondamentos para exibição
            val amortization = rawAmortization.roundTwo()
            val interest = rawInterest.roundTwo()
            val extraAmount = extraRaw.roundTwo()
            val installmentValue = (rawInterest + rawAmortization).roundTwo()

            // Saldo residual desprezível deve ser zerado
            val remainingDisplay = if (balanceAfterAmortization < BALANCE_EPSILON) {
                0.0
            } else {
                balanceAfterAmortization.roundTwo()
            }

            installments.add(
                Installment(
                    month = month,
                    amortization = amortization,
                    interest = interest,
                    installment = installmentValue,
                    remainingBalance = remainingDisplay,
                    extraAmortization = extraAmount
                )
            )

            remainingBalance = balanceAfterAmortization

            // Se quitou, encerra
            if (remainingBalance < BALANCE_EPSILON) {
                break
            }

            // Recalcula após amortização extra
            if (extraAmount > 0.0) {
                if (shouldReduceTerm) {
                    // Reduz prazo: recalcula meses restantes
                    val remainingMonths = calculateRemainingMonths(
                        balance = remainingBalance,
                        monthlyRate = monthlyRate,
                        payment = payment
                    )
                    val candidateTerms = month + remainingMonths
                    effectiveTerms = min(effectiveTerms, candidateTerms)
                } else {
                    // Reduz pagamento: mantém prazo EFETIVO, recalcula parcela
                    // Usa effectiveTerms para respeitar reduções de prazo anteriores
                    val monthsLeft = (effectiveTerms - month).coerceAtLeast(1)
                    payment = computePayment(remainingBalance, monthlyRate, monthsLeft)
                }
            }

            month++
        }

        return installments
    }

    private fun computePayment(
        balance: Double,
        monthlyRate: Double,
        months: Int
    ): Double {
        // Proteções contra entradas inválidas
        if (months <= 0 || balance <= 0.0) {
            return if (balance > 0.0) balance else 0.0
        }

        // Taxa zero ou muito próxima: divisão simples
        if (abs(monthlyRate) < MATH_EPSILON) {
            return balance / months
        }

        val numerator = balance * monthlyRate
        val factor = (1 + monthlyRate).pow(-months)
        val denominator = 1 - factor

        // Proteção contra valores matematicamente inválidos
        // Usa epsilon MUITO menor para cálculos internos
        if (abs(denominator) < MATH_EPSILON) {
            return balance / months
        }

        val result = numerator / denominator

        // Proteção adicional: se resultado for absurdo, usa fallback
        if (!result.isFinite() || result <= 0.0 || result > balance) {
            return balance / months
        }

        return result
    }

    private fun calculateRemainingMonths(
        balance: Double,
        monthlyRate: Double,
        payment: Double
    ): Int {
        // Saldo desprezível
        if (balance < BALANCE_EPSILON) return 0

        // Pagamento inválido
        if (payment <= 0.0) return Int.MAX_VALUE

        // Taxa zero: divisão simples
        if (abs(monthlyRate) < MATH_EPSILON) {
            return ceil(balance / payment).toInt()
        }

        val interestPortion = monthlyRate * balance

        // Se o pagamento não cobre nem os juros
        if (payment <= interestPortion) {
            return Int.MAX_VALUE
        }

        val ratio = payment / (payment - interestPortion)

        // Proteção contra valores inválidos
        if (ratio <= 0.0 || !ratio.isFinite()) return Int.MAX_VALUE

        val numerator = ln(ratio)
        val denominator = ln(1 + monthlyRate)

        // Proteção matemática
        if (abs(denominator) < MATH_EPSILON) return Int.MAX_VALUE

        val months = numerator / denominator

        // Proteção contra valores negativos ou infinitos
        if (!months.isFinite() || months < 0) return Int.MAX_VALUE

        return ceil(months).toInt()
    }
}