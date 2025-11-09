package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.Installment
import com.elab.amortizaplus.domain.util.MathUtils.isEffectivelyZero
import com.elab.amortizaplus.domain.util.MathUtils.nonNegative
import com.elab.amortizaplus.domain.util.MathUtils.roundTwo
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.pow

/**
 * Calculadora para o sistema PRICE (Tabela Francesa)
 */
class PriceCalculator {

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

        while (month <= effectiveTerms && !remainingBalance.isEffectivelyZero()) {

            val extraInput = extraAmortizations[month]
            val requestedExtra = extraInput?.amount ?: 0.0
            val shouldReduceTerm = extraInput?.reduceTerm ?: true

            val rawInterest = remainingBalance * monthlyRate
            var rawAmortization = payment - rawInterest
            if (rawAmortization < 0.0) rawAmortization = 0.0
            rawAmortization = min(rawAmortization, remainingBalance)

            var balanceAfterAmortization = (remainingBalance - rawAmortization).nonNegative()
            val extraRaw = min(requestedExtra, balanceAfterAmortization)
            balanceAfterAmortization -= extraRaw

            val installmentValue = (rawInterest + rawAmortization).roundTwo()
            val amortization = rawAmortization.roundTwo()
            val interest = rawInterest.roundTwo()
            val extraAmount = extraRaw.roundTwo()
            val remainingDisplay = balanceAfterAmortization.roundTwo()

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

            if (remainingBalance.isEffectivelyZero()) break

            if (extraAmount > 0.0) {
                if (shouldReduceTerm) {
                    val remainingMonths = calculateRemainingMonths(
                        balance = remainingBalance,
                        monthlyRate = monthlyRate,
                        payment = payment
                    )
                    val candidateTerms = month + remainingMonths
                    effectiveTerms = min(effectiveTerms, candidateTerms)
                } else {
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
        if (months <= 0) return balance
        if (monthlyRate.isEffectivelyZero()) {
            return balance / months
        }

        val numerator = balance * monthlyRate
        val denominator = 1 - (1 + monthlyRate).pow(-months)

        if (denominator == 0.0) {
            return balance
        }

        return numerator / denominator
    }

    private fun calculateRemainingMonths(
        balance: Double,
        monthlyRate: Double,
        payment: Double
    ): Int {
        if (balance.isEffectivelyZero()) return 0

        if (monthlyRate.isEffectivelyZero()) {
            return ceil(balance / payment).toInt()
        }

        val interestPortion = monthlyRate * balance
        if (payment <= interestPortion) {
            return Int.MAX_VALUE
        }

        val numerator = ln(payment / (payment - interestPortion))
        val denominator = ln(1 + monthlyRate)
        if (denominator == 0.0) return Int.MAX_VALUE

        return ceil(numerator / denominator).toInt()
    }
}
