package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.elab.amortizaplus.presentation.ds.components.AppTopBar
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationErrorSection
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationFormSection
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationInitialSection
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationLoadingSection
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts

@Composable
fun SimulationFormScreen(
    formState: SimulationFormState,
    actions: SimulationFormActions,
    status: SimulationFormStatus,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = SimulationTexts.screenTitle
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
            SimulationFormSection(
                formState = formState,
                actions = actions,
                isLoading = status is SimulationFormStatus.Loading
            )

            when (val state = status) {
                is SimulationFormStatus.Initial -> SimulationInitialSection()
                is SimulationFormStatus.Loading -> SimulationLoadingSection()
                is SimulationFormStatus.Error -> SimulationErrorSection(
                    message = state.message,
                    onRetry = onRetry
                )
            }
        }
    }
}
