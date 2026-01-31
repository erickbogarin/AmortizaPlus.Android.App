package com.elab.amortizaplus.presentation.screens.simulation

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.ExtraAmortization
import com.elab.amortizaplus.domain.model.InterestRateType

data class SimulationInputData(
    val loanAmount: Double,
    val interestRate: Double,
    val rateType: InterestRateType,
    val terms: Int,
    val system: AmortizationSystem,
    val startDate: String,
    val extraAmortizations: List<ExtraAmortization>
)
