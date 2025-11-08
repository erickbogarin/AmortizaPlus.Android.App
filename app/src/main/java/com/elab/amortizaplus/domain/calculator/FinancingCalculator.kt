package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.Installment
import com.elab.amortizaplus.domain.model.InterestRate
import com.elab.amortizaplus.domain.model.SimulationSummary
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Calculadora de financiamento com suporte a SAC e Price,
 * incluindo amortização extra e taxas anuais/mensais.
 */
class FinancingCalculator {

    private val sacCalculator = SacCalculator()

    fun calculate(
        loanAmount: Double,
        rate: InterestRate,
        terms: Int,
        system: AmortizationSystem,
        extraAmortizations: Map<Int, Double> = emptyMap(),
        reduceTerm: Boolean = true
    ): List<Installment> {
        val monthlyRate = rate.asMonthly()
        return when (system) {
            AmortizationSystem.SAC -> sacCalculator.calculate(
                loanAmount = loanAmount,
                monthlyRate = monthlyRate,
                terms = terms,
                extraAmortizations = extraAmortizations,
                reduceTerm = reduceTerm
            )
            AmortizationSystem.PRICE -> calculatePriceSystem(
                loanAmount,
                monthlyRate,
                terms
            )
        }
    }

    fun compare(
        loanAmount: Double,
        rate: InterestRate,
        terms: Int,
        system: AmortizationSystem,
        extraAmortizations: Map<Int, Double> = emptyMap(),
        reduceTerm: Boolean = true
    ): Pair<SimulationSummary, SimulationSummary> {
        val installmentsWithout = calculate(loanAmount, rate, terms, system, emptyMap(), false)
        val installmentsWith = calculate(loanAmount, rate, terms, system, extraAmortizations, reduceTerm)
        return installmentsWithout.toSummary(system) to installmentsWith.toSummary(system)
    }

    private fun calculatePriceSystem(
        loanAmount: Double,
        monthlyRate: Double,
        terms: Int
    ): List<Installment> {
        val installments = mutableListOf<Installment>()
        val factor = (monthlyRate * (1 + monthlyRate).pow(terms)) / ((1 + monthlyRate).pow(terms) - 1)
        val fixedInstallment = loanAmount * factor

        var balance = loanAmount
        for (month in 1..terms) {
            val interest = balance * monthlyRate
            val amortization = fixedInstallment - interest
            balance -= amortization
            installments.add(
                Installment(
                    month = month,
                    amortization = amortization,
                    interest = interest,
                    installment = fixedInstallment,
                    remainingBalance = maxOf(0.0, balance),
                    extraAmortization = 0.0
                )
            )
        }
        return installments
    }

    private fun List<Installment>.toSummary(system: AmortizationSystem): SimulationSummary {
        val totalPaid = sumOf { it.installment + it.extraAmortization }
        val totalInterest = sumOf { it.interest }
        val totalAmortized = sumOf { it.amortization + it.extraAmortization }

        return SimulationSummary(
            system = system,
            totalPaid = totalPaid.roundTwo(),
            totalInterest = totalInterest.roundTwo(),
            totalAmortized = totalAmortized.roundTwo(),
            totalMonths = size
        )
    }

    private fun Double.roundTwo(): Double = (this * 100).roundToInt() / 100.0
}
