package com.elab.amortizaplus.presentation.screens.simulation

import com.elab.amortizaplus.domain.model.Installment
import com.elab.amortizaplus.domain.model.SimulationSummary

sealed class SimulationUiState {
    data object Initial : SimulationUiState()
    data object Loading : SimulationUiState()

    data class Success(
        val inputData: SimulationInputData,
        val summaryWithout: SimulationSummary,
        val summaryWith: SimulationSummary,
        val installmentsWithout: List<Installment>,
        val installmentsWith: List<Installment>,
    ) : SimulationUiState()

    data class Error(val message: String) : SimulationUiState()
}