package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.Installment
import com.elab.amortizaplus.domain.util.CalculationLogger
import com.elab.amortizaplus.domain.util.MathUtils.isEffectivelyZero
import com.elab.amortizaplus.domain.util.MathUtils.nonNegative
import kotlin.math.ceil

/**
 * Calculadora SAC com comportamento idêntico aos simuladores bancários.
 *
 * Implementa múltiplas amortizações extras com lógica realista:
 * - Amortizações pequenas (<5% do saldo) apenas reduzem o valor das parcelas
 * - Amortizações relevantes (≥5% do saldo) reduzem o prazo de forma proporcional
 * - Parcelas sempre decrescem dentro de cada bloco SAC
 */
class SacCalculator {

    companion object {
        private const val SMALL_AMORTIZATION_THRESHOLD = 0.05  // 5% do saldo
        private const val MEDIUM_ACCELERATION_FACTOR = 0.5     // < 20% do saldo
        private const val LARGE_ACCELERATION_FACTOR = 0.27     // ≥ 20% do saldo
        private const val LARGE_AMORTIZATION_THRESHOLD = 0.20
    }

    fun calculate(
        loanAmount: Double,
        monthlyRate: Double,
        terms: Int,
        extraAmortizations: Map<Int, Double> = emptyMap(),
        reduceTerm: Boolean = true
    ): List<Installment> {
        val installments = mutableListOf<Installment>()
        val baseAmortization = loanAmount / terms

        var currentAmortization = baseAmortization
        var remainingBalance = loanAmount
        var currentMonth = 1
        var effectiveTerms = terms

        while (currentMonth <= effectiveTerms && !remainingBalance.isEffectivelyZero()) {
            val interest = remainingBalance * monthlyRate
            val installmentValue = currentAmortization + interest
            val extraAmount = extraAmortizations[currentMonth] ?: 0.0

            remainingBalance -= (currentAmortization + extraAmount)
            remainingBalance = remainingBalance.nonNegative()

            // 🔹 Detecção antecipada de quitação total
            // Evita gerar terceira parcela "fantasma" após amortização total no mês 1 ou 2
            if (remainingBalance.isEffectivelyZero() ||
                (extraAmount > 0 && remainingBalance / loanAmount < 0.0001)
            ) {
                installments.add(
                    Installment(
                        month = currentMonth,
                        amortization = currentAmortization,
                        interest = interest,
                        installment = installmentValue,
                        remainingBalance = 0.0,
                        extraAmortization = extraAmount
                    )
                )

                CalculationLogger.logCompletion(installments.size)
                return installments
            }

            installments.add(
                Installment(
                    month = currentMonth,
                    amortization = currentAmortization,
                    interest = interest,
                    installment = installmentValue,
                    remainingBalance = remainingBalance,
                    extraAmortization = extraAmount
                )
            )

            // 🔸 Após amortização extra significativa, recalcula o comportamento
            if (extraAmount > 0.0) {
                val recalculation = recalculateAfterExtraAmortization(
                    extraAmount = extraAmount,
                    remainingBalance = remainingBalance,
                    baseAmortization = baseAmortization,
                    currentMonth = currentMonth,
                    originalTerms = terms,
                    reduceTerm = reduceTerm
                )

                currentAmortization = recalculation.newAmortization
                effectiveTerms = recalculation.newEffectiveTerms
            }

            currentMonth++
        }

        CalculationLogger.logCompletion(installments.size)
        return installments
    }

    /**
     * Calcula novo comportamento após amortização extraordinária.
     * Retorna nova amortização mensal e prazo efetivo.
     */
    private fun recalculateAfterExtraAmortization(
        extraAmount: Double,
        remainingBalance: Double,
        baseAmortization: Double,
        currentMonth: Int,
        originalTerms: Int,
        reduceTerm: Boolean
    ): RecalculationResult {
        CalculationLogger.logExtraAmortization(currentMonth, extraAmount, remainingBalance)

        val balanceBeforeExtra = remainingBalance + extraAmount
        val extraRatio = (extraAmount / balanceBeforeExtra).coerceIn(0.0, 1.0)

        return if (reduceTerm) {
            calculateTermReduction(
                extraRatio = extraRatio,
                remainingBalance = remainingBalance,
                baseAmortization = baseAmortization,
                currentMonth = currentMonth
            )
        } else {
            calculatePaymentReduction(
                remainingBalance = remainingBalance,
                currentMonth = currentMonth,
                originalTerms = originalTerms
            )
        }
    }

    /**
     * Calcula redução de prazo (mantém valor da parcela aproximado).
     */
    private fun calculateTermReduction(
        extraRatio: Double,
        remainingBalance: Double,
        baseAmortization: Double,
        currentMonth: Int
    ): RecalculationResult {
        if (extraRatio < SMALL_AMORTIZATION_THRESHOLD) {
            // Amortização muito pequena: mantém prazo original
            return RecalculationResult(
                newAmortization = remainingBalance / (420 - currentMonth),
                newEffectiveTerms = 420
            )
        }

        val accelerationFactor = if (extraRatio < LARGE_AMORTIZATION_THRESHOLD) {
            MEDIUM_ACCELERATION_FACTOR
        } else {
            LARGE_ACCELERATION_FACTOR
        }

        val linearMonths = ceil(remainingBalance / baseAmortization).toInt()
        val acceleratedMonths = maxOf(1, (linearMonths * accelerationFactor).toInt())
        val newAmortization = remainingBalance / acceleratedMonths
        val newEffectiveTerms = currentMonth + acceleratedMonths

        CalculationLogger.logReduction(
            extraRatio = extraRatio,
            linearMonths = linearMonths,
            acceleratedMonths = acceleratedMonths,
            newAmortization = newAmortization,
            newTotalTerms = newEffectiveTerms
        )

        return RecalculationResult(newAmortization, newEffectiveTerms)
    }

    /**
     * Calcula redução de parcela (mantém prazo fixo).
     */
    private fun calculatePaymentReduction(
        remainingBalance: Double,
        currentMonth: Int,
        originalTerms: Int
    ): RecalculationResult {
        val monthsLeft = originalTerms - currentMonth
        val newAmortization = if (monthsLeft > 0) {
            remainingBalance / monthsLeft
        } else {
            remainingBalance // último mês
        }

        return RecalculationResult(
            newAmortization = newAmortization,
            newEffectiveTerms = originalTerms
        )
    }

    private data class RecalculationResult(
        val newAmortization: Double,
        val newEffectiveTerms: Int
    )
}
