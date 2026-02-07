package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Surface
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.elab.amortizaplus.presentation.ds.components.AppTopBar
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.foundation.AppDimens
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
                title = "Amortiza+"
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = AppDimens.elevationSmall
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = AppSpacing.medium, vertical = AppSpacing.small)
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    AppButton(
                        text = SimulationTexts.calculateButton,
                        onClick = actions.onCalculate,
                        enabled = formState.isValid() && status !is SimulationFormStatus.Loading,
                        isLoading = status is SimulationFormStatus.Loading
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.medium)
        ) {
            SimulationFormSection(
                formState = formState,
                actions = actions,
                isLoading = status is SimulationFormStatus.Loading
            )

            Spacer(Modifier.height(AppSpacing.small))

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
