package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(SimulationTexts.screenTitle) },
                actions = {
                    IconButton(onClick = onViewHistory) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = SimulationTexts.historyTitle
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is SimulationUiState.Result -> SimulationResultScreen(
                summaryWithout = state.summaryWithout,
                summaryWith = state.summaryWith,
                onViewDetails = onViewDetails,
                onEditSimulation = viewModel::onEditSimulation,
                onNewSimulation = viewModel::onNewSimulation,
                contentPadding = padding
            )
            is SimulationUiState.Form -> SimulationFormScreen(
                formState = formState,
                actions = actions,
                status = state.status,
                onRetry = viewModel::onCalculateClicked,
                contentPadding = padding
            )
        }
    }

    LaunchedEffect(selectedSimulationId) {
        selectedSimulationId?.takeIf { it.isNotBlank() }?.let { simulationId ->
            viewModel.loadSavedSimulation(simulationId)
        }
    }
}
