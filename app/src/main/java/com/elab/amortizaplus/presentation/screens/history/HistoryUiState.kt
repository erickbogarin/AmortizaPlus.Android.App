package com.elab.amortizaplus.presentation.screens.history

import com.elab.amortizaplus.domain.model.SavedSimulation

sealed class HistoryUiState {
    data object Initial : HistoryUiState()
    data object Loading : HistoryUiState()
    data object Empty : HistoryUiState()
    data class Success(val simulations: List<SavedSimulation>) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}

sealed class HistoryDetailUiState {
    data object Initial : HistoryDetailUiState()
    data object Loading : HistoryDetailUiState()
    data class Success(val simulation: SavedSimulation) : HistoryDetailUiState()
    data class Error(val message: String) : HistoryDetailUiState()
}
