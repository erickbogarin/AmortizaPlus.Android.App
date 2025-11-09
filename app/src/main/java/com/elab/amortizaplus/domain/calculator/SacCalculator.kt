package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.Installment
import com.elab.amortizaplus.domain.util.CalculationLogger
import com.elab.amortizaplus.domain.util.MathUtils.isEffectivelyZero
import com.elab.amortizaplus.domain.util.MathUtils.nonNegative
import com.elab.amortizaplus.domain.util.MathUtils.roundTwo
import kotlin.math.ceil
import kotlin.math.min

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
        extraAmortizations: Map<Int, ExtraAmortizationInput> = emptyMap()
    ): List<Installment> {
        val installments = mutableListOf<Installment>()
        val baseAmortization = loanAmount / terms

        var currentAmortization = baseAmortization
        var remainingBalance = loanAmount
        var currentMonth = 1
        var effectiveTerms = terms

        while (currentMonth <= effectiveTerms && !remainingBalance.isEffectivelyZero()) {
            val extraInput = extraAmortizations[currentMonth]
            val appliedReduceTerm = extraInput?.reduceTerm ?: true
            val requestedExtra = extraInput?.amount ?: 0.0

            val rawInterest = remainingBalance * monthlyRate
            val interest = rawInterest.roundTwo()
            val amortizationRaw = min(currentAmortization, remainingBalance)
            var balanceAfterAmortization = (remainingBalance - amortizationRaw).nonNegative()

            var extraRaw = 0.0
            if (requestedExtra > 0.0) {
                extraRaw = min(requestedExtra, balanceAfterAmortization)
                balanceAfterAmortization -= extraRaw
            }

            val amortization = amortizationRaw.roundTwo()
            val extraAmount = extraRaw.roundTwo()
            val installmentValue = (amortizationRaw + rawInterest).roundTwo()
            val displayedBalance = balanceAfterAmortization.roundTwo()

            if (displayedBalance.isEffectivelyZero() ||
                (extraAmount > 0 && displayedBalance / loanAmount < 0.0001)
            ) {
                installments.add(
                    Installment(
                        month = currentMonth,
                        amortization = amortization,
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
                    amortization = amortization,
                    interest = interest,
                    installment = installmentValue,
                    remainingBalance = displayedBalance,
                    extraAmortization = extraAmount
                )
            )

            remainingBalance = balanceAfterAmortization

            if (extraAmount > 0.0) {
                val recalculation = recalculateAfterExtraAmortization(
                    extraAmount = extraAmount,
                    remainingBalance = remainingBalance,
                    baseAmortization = baseAmortization,
                    currentMonth = currentMonth,
                    currentEffectiveTerms = effectiveTerms,
                    reduceTerm = appliedReduceTerm
                )

                currentAmortization = recalculation.newAmortization
                effectiveTerms = min(effectiveTerms, recalculation.newEffectiveTerms)
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
        currentEffectiveTerms: Int,
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
                currentMonth = currentMonth,
                currentEffectiveTerms = currentEffectiveTerms
            )
        } else {
            calculatePaymentReduction(
                remainingBalance = remainingBalance,
                currentMonth = currentMonth,
                currentEffectiveTerms = currentEffectiveTerms
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
        currentMonth: Int,
        currentEffectiveTerms: Int
    ): RecalculationResult {
        if (extraRatio < SMALL_AMORTIZATION_THRESHOLD) {
            // Amortização muito pequena: mantém prazo original
            val monthsLeft = (currentEffectiveTerms - currentMonth).coerceAtLeast(1)
            return RecalculationResult(
                newAmortization = remainingBalance / monthsLeft,
                newEffectiveTerms = currentEffectiveTerms
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
        val candidateTerms = currentMonth + acceleratedMonths
        val newEffectiveTerms = min(currentEffectiveTerms, candidateTerms)

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
        currentEffectiveTerms: Int
    ): RecalculationResult {
        val monthsLeft = (currentEffectiveTerms - currentMonth).coerceAtLeast(1)
        val newAmortization = if (monthsLeft > 0) {
            remainingBalance / monthsLeft
        } else {
            remainingBalance // último mês
        }

        return RecalculationResult(
            newAmortization = newAmortization,
            newEffectiveTerms = currentEffectiveTerms
        )
    }

    private data class RecalculationResult(
        val newAmortization: Double,
        val newEffectiveTerms: Int
    )
}
