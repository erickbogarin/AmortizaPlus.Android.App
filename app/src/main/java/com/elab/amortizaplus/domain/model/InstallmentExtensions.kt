package com.elab.amortizaplus.domain.model

import com.elab.amortizaplus.domain.util.MathUtils.roundTwo

/**
 * Extensions para List<Installment> que centralizam cálculos de resumo.
 * Elimina duplicação entre calculadoras e facilita testes.
 */

/**
 * Converte uma lista de parcelas em resumo financeiro.
 */
fun List<Installment>.toSummary(system: AmortizationSystem? = null): SimulationSummary {
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

/**
 * Calcula a economia entre duas simulações.
 */
fun SimulationSummary.calculateSavingsComparedTo(baseline: SimulationSummary): SimulationSummary {
    val reducedMonths = baseline.totalMonths - this.totalMonths
    val interestSavings = baseline.totalInterest - this.totalInterest

    return this.copy(
        reducedMonths = reducedMonths,
        interestSavings = interestSavings.roundTwo()
    )
}

/**
 * Valida se a simulação está quitada (saldo final próximo de zero).
 */
fun List<Installment>.isFullyPaid(epsilon: Double = 0.01): Boolean =
    lastOrNull()?.remainingBalance?.let { it < epsilon } ?: false

/**
 * Retorna o total de amortizações extras realizadas.
 */
fun List<Installment>.totalExtraAmortizations(): Double =
    sumOf { it.extraAmortization }
