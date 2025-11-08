package com.elab.amortizaplus.domain.model

data class SimulationSummary(
    val system: AmortizationSystem? = null,
    val totalPaid: Double,
    val totalInterest: Double,
    val totalAmortized: Double,
    val totalMonths: Int,
    val reducedMonths: Int = 0,
    val interestSavings: Double = 0.0
)