package com.elab.amortizaplus.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elab.amortizaplus.domain.usecase.GetSimulationByIdUseCase
import com.elab.amortizaplus.domain.usecase.GetSimulationHistoryUseCase
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val getSimulationHistoryUseCase: GetSimulationHistoryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Initial)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    private var observeJob: Job? = null

    init {
        observeHistory()
    }

    fun retry() {
        observeHistory()
    }

    private fun observeHistory() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            getSimulationHistoryUseCase()
                .catch {
                    _uiState.value = HistoryUiState.Error(
                        SimulationTexts.historyLoadError
                    )
                }
                .collect { items ->
                    _uiState.value = if (items.isEmpty()) {
                        HistoryUiState.Empty
                    } else {
                        HistoryUiState.Success(items)
                    }
                }
        }
    }
}

class HistoryDetailViewModel(
    private val getSimulationByIdUseCase: GetSimulationByIdUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<HistoryDetailUiState>(HistoryDetailUiState.Initial)
    val uiState: StateFlow<HistoryDetailUiState> = _uiState.asStateFlow()

    fun load(id: String) {
        if (id.isBlank()) {
            _uiState.value = HistoryDetailUiState.Error(SimulationTexts.historyNotFound)
            return
        }

        viewModelScope.launch {
            _uiState.value = HistoryDetailUiState.Loading
            val result = getSimulationByIdUseCase(id)

            result.fold(
                onSuccess = { saved ->
                    _uiState.value = if (saved == null) {
                        HistoryDetailUiState.Error(SimulationTexts.historyNotFound)
                    } else {
                        HistoryDetailUiState.Success(saved)
                    }
                },
                onFailure = {
                    _uiState.value = HistoryDetailUiState.Error(SimulationTexts.historyLoadError)
                }
            )
        }
    }
}
