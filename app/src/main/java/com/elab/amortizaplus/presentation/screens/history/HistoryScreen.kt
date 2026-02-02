package com.elab.amortizaplus.presentation.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.elab.amortizaplus.domain.model.SavedSimulation
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.AppCard
import com.elab.amortizaplus.presentation.ds.components.AppInfoCard
import com.elab.amortizaplus.presentation.ds.components.AppLoadingIndicator
import com.elab.amortizaplus.presentation.ds.components.ButtonVariant
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import com.elab.amortizaplus.presentation.util.formatTerms
import com.elab.amortizaplus.presentation.util.toCurrencyBR
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onSimulationClick: (String) -> Unit,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(SimulationTexts.historyTitle) },
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
                is HistoryUiState.Initial,
                is HistoryUiState.Loading -> AppLoadingIndicator(
                    message = SimulationTexts.historyLoadingMessage
                )

                is HistoryUiState.Empty -> AppInfoCard {
                    Text(
                        text = SimulationTexts.historyEmptyTitle,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = SimulationTexts.historyEmptyMessage,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                is HistoryUiState.Error -> AppInfoCard {
                    Text(
                        text = SimulationTexts.historyErrorTitle,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    AppButton(
                        text = SimulationTexts.retryButton,
                        onClick = viewModel::retry,
                        variant = ButtonVariant.Secondary
                    )
                }

                is HistoryUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
                    ) {
                        items(
                            items = state.simulations,
                            key = { it.id }
                        ) { saved ->
                            HistoryItemCard(
                                saved = saved,
                                onClick = { onSimulationClick(saved.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    saved: SavedSimulation,
    onClick: () -> Unit
) {
    AppCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = saved.name,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "${saved.simulation.amortizationSystem.name} • ${saved.simulation.loanAmount.toCurrencyBR()} • ${saved.simulation.terms.formatTerms()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${SimulationTexts.historyTotalPaidLabel}: ${saved.result.summaryWithExtra.totalPaid.toCurrencyBR()}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${SimulationTexts.historyLastModifiedLabel}: ${saved.lastModified.toDateTimeBr()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun Long.toDateTimeBr(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(this))
}
