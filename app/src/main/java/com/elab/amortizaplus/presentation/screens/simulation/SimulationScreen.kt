package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStoreOwner
import org.koin.androidx.compose.koinViewModel
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationLoadingSection

@Composable
fun SimulationFormRoute(
    selectedSimulationId: String?,
    parentEntry: ViewModelStoreOwner,
    onNavigateToResult: () -> Unit,
    onNavigateToResultWithId: (String, String?) -> Unit,
    returnToRoute: String?
) {
    val viewModel: SimulationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val actions = remember(viewModel) {
        SimulationFormActions.from(viewModel)
    }

    val hasResult = uiState is SimulationUiState.Result
    if (hasResult) {
        LaunchedEffect(uiState) {
            if (returnToRoute.isNullOrBlank()) {
                onNavigateToResult()
            } else {
                onNavigateToResultWithId("", returnToRoute)
            }
        }
    }

    val formStateUi = uiState as? SimulationUiState.Form
    if (!hasResult) {
        SimulationFormScreen(
            formState = formState,
            actions = actions,
            status = formStateUi?.status ?: SimulationFormStatus.Initial,
            onRetry = viewModel::onCalculateClicked
        )
    } else {
        SimulationLoadingSection()
    }

    LaunchedEffect(selectedSimulationId) {
        selectedSimulationId?.takeIf { it.isNotBlank() }?.let { simulationId ->
            viewModel.loadSavedSimulation(simulationId)
        }
    }
}

@Composable
fun SimulationResultRoute(
    parentEntry: ViewModelStoreOwner,
    onViewDetails: () -> Unit,
    onEditSimulation: () -> Unit,
    onNewSimulation: () -> Unit,
    selectedSimulationId: String?,
    returnToRoute: String?
) {
    val viewModel: SimulationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState as? SimulationUiState.Result

    if (state == null) {
        if (!returnToRoute.isNullOrBlank()) {
            LaunchedEffect(returnToRoute) { onEditSimulation() }
            return
        }
        LaunchedEffect(selectedSimulationId) {
            selectedSimulationId?.takeIf { it.isNotBlank() }?.let { simulationId ->
                viewModel.loadSavedSimulation(simulationId)
            }
        }
        SimulationLoadingSection()
        return
    }

    SimulationResultScreen(
        summaryWithout = state.summaryWithout,
        summaryWith = state.summaryWith,
        onViewDetails = onViewDetails,
        onEditSimulation = {
            viewModel.onEditSimulation()
            onEditSimulation()
        },
        onNewSimulation = {
            viewModel.onNewSimulation()
            onNewSimulation()
        }
    )
}
