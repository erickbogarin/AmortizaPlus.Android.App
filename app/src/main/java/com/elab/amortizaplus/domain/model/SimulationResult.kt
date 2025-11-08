package com.elab.amortizaplus.domain.model

data class SimulationResult(
    val simulation: Simulation,
    val paymentsWithoutExtra: List<Installment>,
    val paymentsWithExtra: List<Installment>,
    val summaryWithoutExtra: SimulationSummary,
    val summaryWithExtra: SimulationSummary
)