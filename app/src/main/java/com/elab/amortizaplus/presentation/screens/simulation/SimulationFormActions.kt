package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.runtime.Immutable
import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.InterestRateType

@Immutable
data class SimulationFormActions(
    val onLoanAmountChange: (String) -> Unit,
    val onInterestRateChange: (String) -> Unit,
    val onTermsChange: (String) -> Unit,
    val onRateTypeChange: (InterestRateType) -> Unit,
    val onSystemChange: (AmortizationSystem) -> Unit,
    val onCalculate: () -> Unit,
) {
    companion object {
        fun from(viewModel: SimulationViewModel) = SimulationFormActions(
            onLoanAmountChange = viewModel::onLoanAmountChange,
            onInterestRateChange = viewModel::onInterestRateChange,
            onTermsChange = viewModel::onTermsChange,
            onRateTypeChange = viewModel::onRateTypeChange,
            onSystemChange = viewModel::onSystemChange,
            onCalculate = viewModel::calculate
        )
    }
}