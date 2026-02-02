package com.elab.amortizaplus.presentation.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.elab.amortizaplus.domain.model.SavedSimulation
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.AppCard
import com.elab.amortizaplus.presentation.ds.components.AppFinancialInfoRow
import com.elab.amortizaplus.presentation.ds.components.AppInfoCard
import com.elab.amortizaplus.presentation.ds.components.AppLoadingIndicator
import com.elab.amortizaplus.presentation.ds.components.ButtonVariant
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import com.elab.amortizaplus.presentation.util.formatTerms
import com.elab.amortizaplus.presentation.util.toCurrencyBR
import com.elab.amortizaplus.presentation.util.toPercent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    simulationId: String,
    onBack: () -> Unit,
    viewModel: HistoryDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(simulationId) {
        viewModel.load(simulationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(SimulationTexts.historyDetailTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = SimulationTexts.historyBackButton
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            when (val state = uiState) {
                is HistoryDetailUiState.Initial,
                is HistoryDetailUiState.Loading -> AppLoadingIndicator(
                    message = SimulationTexts.historyLoadingMessage
                )

                is HistoryDetailUiState.Error -> AppInfoCard {
                    Text(
                        text = SimulationTexts.historyErrorTitle,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    AppButton(
                        text = SimulationTexts.historyBackButton,
                        onClick = onBack,
                        variant = ButtonVariant.Secondary
                    )
                }

                is HistoryDetailUiState.Success -> HistoryDetailContent(
                    saved = state.simulation,
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
private fun HistoryDetailContent(
    saved: SavedSimulation,
    onBack: () -> Unit
) {
    AppCard {
        Text(
            text = saved.name,
            style = MaterialTheme.typography.titleMedium
        )
        AppFinancialInfoRow(
            label = SimulationTexts.systemLabel,
            value = saved.simulation.amortizationSystem.name
        )
        AppFinancialInfoRow(
            label = SimulationTexts.valueLabel,
            value = saved.simulation.loanAmount.toCurrencyBR()
        )
        AppFinancialInfoRow(
            label = SimulationTexts.rateLabel,
            value = saved.simulation.interestRate.toPercent()
        )
        AppFinancialInfoRow(
            label = SimulationTexts.termLabel,
            value = saved.simulation.terms.formatTerms()
        )
    }

    AppCard {
        Text(
            text = SimulationTexts.historyResultTitle,
            style = MaterialTheme.typography.titleSmall
        )
        AppFinancialInfoRow(
            label = SimulationTexts.totalPaidLabel,
            value = saved.result.summaryWithExtra.totalPaid.toCurrencyBR()
        )
        AppFinancialInfoRow(
            label = SimulationTexts.totalInterestLabel,
            value = saved.result.summaryWithExtra.totalInterest.toCurrencyBR()
        )
        AppFinancialInfoRow(
            label = SimulationTexts.monthsLabel,
            value = saved.result.summaryWithExtra.totalMonths.formatTerms()
        )
    }

    AppButton(
        text = SimulationTexts.historyBackButton,
        onClick = onBack,
        variant = ButtonVariant.Secondary
    )
}
