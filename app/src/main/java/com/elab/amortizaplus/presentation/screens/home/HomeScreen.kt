package com.elab.amortizaplus.presentation.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.AppCard
import com.elab.amortizaplus.presentation.ds.components.AppTopBar
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts

@Composable
fun HomeScreen(
    onStartSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = SimulationTexts.homeTitle
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppSpacing.medium)
        ) {
            Text(text = SimulationTexts.homePlaceholder)
            Text(
                text = SimulationTexts.homeMicrocopy,
                modifier = Modifier.padding(top = AppSpacing.small)
            )
            AppButton(
                text = SimulationTexts.newSimulationButton,
                onClick = onStartSimulation,
                modifier = Modifier.padding(top = AppSpacing.medium)
            )
            Column(
                modifier = Modifier.padding(top = AppSpacing.large)
            ) {
                Text(text = SimulationTexts.homeLearnSectionTitle)
                AppCard(
                    modifier = Modifier.padding(top = AppSpacing.small)
                ) {
                    Text(text = SimulationTexts.homeLearnCard1Title)
                    Text(
                        text = SimulationTexts.homeLearnCard1Body,
                        modifier = Modifier.padding(top = AppSpacing.extraSmall)
                    )
                }
                AppCard(
                    modifier = Modifier.padding(top = AppSpacing.small)
                ) {
                    Text(text = SimulationTexts.homeLearnCard2Title)
                    Text(
                        text = SimulationTexts.homeLearnCard2Body,
                        modifier = Modifier.padding(top = AppSpacing.extraSmall)
                    )
                }
            }
        }
    }
}
