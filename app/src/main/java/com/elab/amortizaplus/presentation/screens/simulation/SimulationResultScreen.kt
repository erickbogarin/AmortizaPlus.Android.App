package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.elab.amortizaplus.domain.model.SimulationSummary
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.AppTopBar
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.sections.SimulationResultSection
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts

@Composable
fun SimulationResultScreen(
    summaryWithout: SimulationSummary,
    summaryWith: SimulationSummary,
    onViewDetails: () -> Unit,
    onEditSimulation: () -> Unit,
    onNewSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = SimulationTexts.resultScreenTitle,
                navigationIcon = {
                    IconButton(onClick = onEditSimulation) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = SimulationTexts.tableBackButton
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = SimulationTexts.moreOptions
                        )
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(SimulationTexts.editSimulationButton) },
                            onClick = {
                                isMenuExpanded = false
                                onEditSimulation()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(SimulationTexts.newSimulationButton) },
                            onClick = {
                                isMenuExpanded = false
                                onNewSimulation()
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = AppSpacing.medium, vertical = AppSpacing.small),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
            ) {
                AppButton(
                    text = SimulationTexts.viewTableButton,
                    onClick = onViewDetails,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.large)
        ) {
            SimulationResultSection(
                summaryWithout = summaryWithout,
                summaryWith = summaryWith
            )
        }
    }
}
