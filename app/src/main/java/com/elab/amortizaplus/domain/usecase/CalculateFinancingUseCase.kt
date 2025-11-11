package com.elab.amortizaplus.domain.usecase

import com.elab.amortizaplus.domain.calculator.ExtraAmortizationInput
import com.elab.amortizaplus.domain.calculator.FinancingCalculator
import com.elab.amortizaplus.domain.model.InterestRate
import com.elab.amortizaplus.domain.model.InterestRateType
import com.elab.amortizaplus.domain.model.Installment
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
    private val financingCalculator: FinancingCalculator
) {

    operator fun invoke(simulation: Simulation): SimulationResult {
        val rate = when (simulation.rateType) {
            InterestRateType.ANNUAL -> InterestRate.Annual(simulation.interestRate)
            InterestRateType.MONTHLY -> InterestRate.Monthly(simulation.interestRate)
        }
        val extrasMap = buildExtrasMap(simulation)

        val paymentsWithoutExtra = runScenario(
            simulation = simulation,
            rate = rate,
            extras = emptyMap()
        )

        val paymentsWithExtra = runScenario(
            simulation = simulation,
            rate = rate,
            extras = extrasMap
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
    private fun runScenario(
        simulation: Simulation,
        rate: InterestRate,
        extras: Map<Int, ExtraAmortizationInput>
    ): List<Installment> = financingCalculator.calculate(
        loanAmount = simulation.loanAmount,
        rate = rate,
        terms = simulation.terms,
        system = simulation.amortizationSystem,
        extraAmortizations = extras
    )

    /**
     * Converte lista de ExtraAmortization em Map<Int, ExtraAmortizationInput>.
     */
    private fun buildExtrasMap(simulation: Simulation): Map<Int, ExtraAmortizationInput> =
        simulation.extraAmortizations.associate { extra ->
            extra.month to ExtraAmortizationInput.from(extra)
        }
}
