package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.Installment
import com.elab.amortizaplus.domain.model.InterestRate
import com.elab.amortizaplus.domain.model.SimulationSummary
import com.elab.amortizaplus.domain.model.calculateSavingsComparedTo
import com.elab.amortizaplus.domain.model.toSummary
import com.elab.amortizaplus.domain.util.MathUtils.nonNegative
import kotlin.math.pow

/**
 * Calculadora principal de financiamento.
 * Orquestra SAC e PRICE, delegando para calculadoras especializadas.
 */
class FinancingCalculator(
    private val sacCalculator: SacCalculator = SacCalculator(),
    private val priceCalculator: PriceCalculator = PriceCalculator()
) {

    /**
     * Calcula um financiamento com sistema de amortização especificado.
     */
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
            AmortizationSystem.PRICE -> priceCalculator.calculate(
                loanAmount = loanAmount,
                monthlyRate = monthlyRate,
                terms = terms,
                extraAmortizations = extraAmortizations,
                reduceTerm = reduceTerm
            )
        }
    }

    /**
     * Compara dois cenários: com e sem amortizações extras.
     * Retorna (resumo sem extras, resumo com extras incluindo economia).
     */
    fun compare(
        loanAmount: Double,
        rate: InterestRate,
        terms: Int,
        system: AmortizationSystem,
        extraAmortizations: Map<Int, Double> = emptyMap(),
        reduceTerm: Boolean = true
    ): Pair<SimulationSummary, SimulationSummary> {
        val installmentsWithout = calculate(
            loanAmount = loanAmount,
            rate = rate,
            terms = terms,
            system = system,
            extraAmortizations = emptyMap(),
            reduceTerm = false // baseline sempre com prazo completo
        )

        val installmentsWith = calculate(
            loanAmount = loanAmount,
            rate = rate,
            terms = terms,
            system = system,
            extraAmortizations = extraAmortizations,
            reduceTerm = reduceTerm
        )

        val summaryWithout = installmentsWithout.toSummary(system)
        val summaryWith = installmentsWith.toSummary(system)
            .calculateSavingsComparedTo(summaryWithout)

        return summaryWithout to summaryWith
    }
}