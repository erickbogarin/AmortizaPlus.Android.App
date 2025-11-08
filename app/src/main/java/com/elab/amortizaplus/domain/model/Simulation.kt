package com.elab.amortizaplus.domain.model

data class Simulation(
    val loanAmount: Double,
    val interestRate: Double,
    val terms: Int,
    val startDate: String,
    val amortizationSystem: AmortizationSystem,
    val extraAmortizations: List<ExtraAmortization>,
    val name: String
)