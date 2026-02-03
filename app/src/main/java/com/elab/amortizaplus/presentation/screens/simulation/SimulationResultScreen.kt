package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.elab.amortizaplus.domain.model.SimulationSummary
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationResultSection

@Composable
fun SimulationResultScreen(
    summaryWithout: SimulationSummary,
    summaryWith: SimulationSummary,
    onViewDetails: () -> Unit,
    onEditSimulation: () -> Unit,
    onNewSimulation: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        SimulationResultSection(
            summaryWithout = summaryWithout,
            summaryWith = summaryWith,
            onViewDetails = onViewDetails,
            onEditSimulation = onEditSimulation,
            onNewSimulation = onNewSimulation
        )
    }
}
