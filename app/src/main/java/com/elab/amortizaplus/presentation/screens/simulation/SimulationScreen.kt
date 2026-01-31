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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationFormSection
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationResultSection
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationErrorSection
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationInitialSection
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationLoadingSection
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
    var showTable by rememberSaveable { mutableStateOf(false) }
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
        if (showTable && uiState is SimulationUiState.Success) {
            val state = uiState as SimulationUiState.Success
            SimulationTableScreen(
                installmentsWithout = state.installmentsWithout,
                installmentsWith = state.installmentsWith,
                onBack = { showTable = false },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
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

                when (val state = uiState) {
                    is SimulationUiState.Initial -> SimulationInitialSection()
                    is SimulationUiState.Loading -> SimulationLoadingSection()
                    is SimulationUiState.Error -> SimulationErrorSection(
                        message = state.message,
                        onRetry = viewModel::calculate,
                        onReset = viewModel::reset
                    )
                    is SimulationUiState.Success -> SimulationResultSection(
                        summaryWithout = state.summaryWithout,
                        summaryWith = state.summaryWith,
                        onViewDetails = { showTable = true },
                        onReset = viewModel::reset
                    )
                }
            }
        }
    }
}
