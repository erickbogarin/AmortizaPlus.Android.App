package com.elab.amortizaplus.domain.usecase

import com.elab.amortizaplus.domain.calculator.ExtraAmortizationCalculator
import com.elab.amortizaplus.domain.calculator.PriceCalculator
import com.elab.amortizaplus.domain.calculator.SacCalculator
import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.Simulation
import com.elab.amortizaplus.domain.model.SimulationResult

/**
 * Responsável por orquestrar o cálculo completo do financiamento.
 * Seleciona o sistema de amortização (SAC/PRICE),
 * aplica amortizações extras e gera os resumos.
 */
class CalculateFinancingUseCase(
    private val sacCalculator: SacCalculator,
    private val priceCalculator: PriceCalculator,
    private val extraAmortizationCalculator: ExtraAmortizationCalculator
) {

    operator fun invoke(simulation: Simulation): SimulationResult {
        // Escolhe a calculadora apropriada
        val installmentsWithoutExtra = when (simulation.amortizationSystem) {
            AmortizationSystem.SAC -> sacCalculator.calculate(
                loanAmount = simulation.loanAmount,
                monthlyRate = simulation.interestRate,
                terms = simulation.terms
            )
            AmortizationSystem.PRICE -> priceCalculator.calculate(
                loanAmount = simulation.loanAmount,
                monthlyRate = simulation.interestRate,
                terms = simulation.terms
            )
        }

        // Converte lista → mapa
        val extrasMap = simulation.extraAmortizations.associate { it.month to it.amount }

        val installmentsWithExtra = when (simulation.amortizationSystem) {
            AmortizationSystem.SAC -> sacCalculator.calculate(
                loanAmount = simulation.loanAmount,
                monthlyRate = simulation.interestRate,
                terms = simulation.terms,
                extraAmortizations = extrasMap
            )
            AmortizationSystem.PRICE -> priceCalculator.calculate(
                loanAmount = simulation.loanAmount,
                monthlyRate = simulation.interestRate,
                terms = simulation.terms,
                extraAmortizations = extrasMap
            )
        }

        // Gera os resumos (agora compatíveis com system opcional)
        val summaryWithout = extraAmortizationCalculator.calculateSummary(installmentsWithoutExtra)
            .copy(system = simulation.amortizationSystem)
        val summaryWith = extraAmortizationCalculator.calculateSummary(installmentsWithExtra)
            .copy(system = simulation.amortizationSystem)

        // Calcula comparativo
        val reducedMonths = summaryWithout.totalMonths - summaryWith.totalMonths
        val interestSavings = summaryWithout.totalInterest - summaryWith.totalInterest

        val summaryWithFinal = summaryWith.copy(
            reducedMonths = reducedMonths,
            interestSavings = interestSavings
        )

        // Retorna resultado completo
        return SimulationResult(
            simulation = simulation,
            paymentsWithoutExtra = installmentsWithoutExtra,
            paymentsWithExtra = installmentsWithExtra,
            summaryWithoutExtra = summaryWithout,
            summaryWithExtra = summaryWithFinal
        )
    }
}