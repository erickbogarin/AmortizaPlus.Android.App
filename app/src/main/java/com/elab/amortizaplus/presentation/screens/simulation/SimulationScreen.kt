package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import org.koin.androidx.compose.koinViewModel

@Composable
fun SimulationScreen(
    onViewDetails: () -> Unit,
    onViewHistory: () -> Unit,
    selectedSimulationId: String? = null,
    viewModel: SimulationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val actions = remember(viewModel) {
        SimulationFormActions.from(viewModel)
    }
    when (val state = uiState) {
        is SimulationUiState.Result -> SimulationResultScreen(
            summaryWithout = state.summaryWithout,
            summaryWith = state.summaryWith,
            onViewDetails = onViewDetails,
            onEditSimulation = viewModel::onEditSimulation,
            onNewSimulation = viewModel::onNewSimulation
        )
        is SimulationUiState.Form -> SimulationFormScreen(
            formState = formState,
            actions = actions,
            status = state.status,
            onRetry = viewModel::onCalculateClicked,
            onViewHistory = onViewHistory
        )
    }

    LaunchedEffect(selectedSimulationId) {
        selectedSimulationId?.takeIf { it.isNotBlank() }?.let { simulationId ->
            viewModel.loadSavedSimulation(simulationId)
        }
    }
}
