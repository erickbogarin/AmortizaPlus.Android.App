package com.elab.amortizaplus.domain.usecase

import com.elab.amortizaplus.domain.calculator.FinancingCalculator
import com.elab.amortizaplus.domain.model.InterestRate
import com.elab.amortizaplus.domain.model.Simulation
import com.elab.amortizaplus.domain.model.SimulationResult
import com.elab.amortizaplus.domain.model.calculateSavingsComparedTo
import com.elab.amortizaplus.domain.model.toSummary

/**
 * Responsável por orquestrar o cálculo completo do financiamento.
 *
 * Entrada: Simulation (parâmetros do financiamento)
 * Saída: SimulationResult (parcelas e resumos comparativos)
 */
class CalculateFinancingUseCase(
    private val financingCalculator: FinancingCalculator = FinancingCalculator()
) {

    operator fun invoke(simulation: Simulation): SimulationResult {
        val rate = InterestRate.Annual(simulation.interestRate)
        val extrasMap = buildExtrasMap(simulation)

        // Cenário sem amortizações (baseline)
        val paymentsWithoutExtra = financingCalculator.calculate(
            loanAmount = simulation.loanAmount,
            rate = rate,
            terms = simulation.terms,
            system = simulation.amortizationSystem,
            extraAmortizations = emptyMap(),
            reduceTerm = false
        )

        // Cenário com amortizações
        val paymentsWithExtra = financingCalculator.calculate(
            loanAmount = simulation.loanAmount,
            rate = rate,
            terms = simulation.terms,
            system = simulation.amortizationSystem,
            extraAmortizations = extrasMap,
            reduceTerm = true // assumimos redução de prazo como padrão
        )

        // Gera resumos
        val summaryWithout = paymentsWithoutExtra.toSummary(simulation.amortizationSystem)
        val summaryWith = paymentsWithExtra.toSummary(simulation.amortizationSystem)
            .calculateSavingsComparedTo(summaryWithout)

        return SimulationResult(
            simulation = simulation,
            paymentsWithoutExtra = paymentsWithoutExtra,
            paymentsWithExtra = paymentsWithExtra,
            summaryWithoutExtra = summaryWithout,
            summaryWithExtra = summaryWith
        )
    }

    /**
     * Converte lista de ExtraAmortization em Map<Int, Double>.
     */
    private fun buildExtrasMap(simulation: Simulation): Map<Int, Double> =
        simulation.extraAmortizations.associate { it.month to it.amount }
}