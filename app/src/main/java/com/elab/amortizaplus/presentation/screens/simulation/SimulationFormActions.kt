package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.runtime.Immutable
import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.ExtraAmortizationStrategy
import com.elab.amortizaplus.domain.model.InterestRateType

@Immutable
data class SimulationFormActions(
    val onLoanAmountChange: (String) -> Unit,
    val onInterestRateChange: (String) -> Unit,
    val onTermsChange: (String) -> Unit,
    val onStartDateChange: (String) -> Unit,
    val onRateTypeChange: (InterestRateType) -> Unit,
    val onSystemChange: (AmortizationSystem) -> Unit,
    val onAddExtraAmortization: () -> Unit,
    val onRemoveExtraAmortization: (Long) -> Unit,
    val onExtraMonthChange: (Long, String) -> Unit,
    val onExtraAmountChange: (Long, String) -> Unit,
    val onExtraStrategyChange: (Long, ExtraAmortizationStrategy) -> Unit,
    val onCalculate: () -> Unit,
) {
    companion object {
        fun from(viewModel: SimulationViewModel) = SimulationFormActions(
            onLoanAmountChange = viewModel::onLoanAmountChange,
            onInterestRateChange = viewModel::onInterestRateChange,
            onTermsChange = viewModel::onTermsChange,
            onStartDateChange = viewModel::onStartDateChange,
            onRateTypeChange = viewModel::onRateTypeChange,
            onSystemChange = viewModel::onSystemChange,
            onAddExtraAmortization = viewModel::addExtraAmortization,
            onRemoveExtraAmortization = viewModel::removeExtraAmortization,
            onExtraMonthChange = viewModel::onExtraMonthChange,
            onExtraAmountChange = viewModel::onExtraAmountChange,
            onExtraStrategyChange = viewModel::onExtraStrategyChange,
            onCalculate = viewModel::calculate
        )
    }
}
