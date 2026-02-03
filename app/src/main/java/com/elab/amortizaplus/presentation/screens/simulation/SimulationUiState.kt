package com.elab.amortizaplus.presentation.screens.simulation

import com.elab.amortizaplus.domain.model.Installment
import com.elab.amortizaplus.domain.model.SimulationSummary

sealed class SimulationUiState {
    data class Form(val status: SimulationFormStatus = SimulationFormStatus.Initial) : SimulationUiState()

    data class Result(
        val inputData: SimulationInputData,
        val summaryWithout: SimulationSummary,
        val summaryWith: SimulationSummary,
        val installmentsWithout: List<Installment>,
        val installmentsWith: List<Installment>,
    ) : SimulationUiState()
}

sealed class SimulationFormStatus {
    data object Initial : SimulationFormStatus()
    data object Loading : SimulationFormStatus()
    data class Error(val message: String) : SimulationFormStatus()
}
