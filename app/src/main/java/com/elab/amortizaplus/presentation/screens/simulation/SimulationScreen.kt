package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationFormSection
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulationScreen(
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
                title = { Text("Simulação de Financiamento") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            // Formulário fixo
            SimulationFormSection(
                formState = formState,
                actions = actions,
                isLoading = uiState is SimulationUiState.Loading
            )

            // Transição reativa das seções
//            AnimatedContent(targetState = uiState, label = "simulation_state") { state ->
//                when (state) {
//                    is SimulationUiState.Initial -> Unit
//                    is SimulationUiState.Loading -> SimulationLoadingSection()
//                    is SimulationUiState.Success -> SimulationResultSection(
//                        inputData = state.inputData,
//                        summaryWithout = state.summaryWithout,
//                        summaryWith = state.summaryWith,
//                        onReset = viewModel::reset
//                    )
//                    is SimulationUiState.Error -> SimulationErrorSection(
//                        message = state.message,
//                        onRetry = viewModel::calculate
//                    )
//                }
//            }
        }
    }
}
