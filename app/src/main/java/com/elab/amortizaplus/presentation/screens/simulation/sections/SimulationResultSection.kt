package com.elab.amortizaplus.presentation.screens.simulation.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.SimulationSummary
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.AppFinancialInfoRow
import com.elab.amortizaplus.presentation.ds.components.AppCard
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import com.elab.amortizaplus.presentation.util.formatTerms
import com.elab.amortizaplus.presentation.util.toCurrencyBR

@Composable
fun SimulationResultSection(
    summaryWithout: SimulationSummary,
    summaryWith: SimulationSummary,
    modifier: Modifier = Modifier,
    onViewDetails: () -> Unit = {},
    onEditSimulation: () -> Unit = {},
    onNewSimulation: () -> Unit = {}
) {
    val showSavings = summaryWith.reducedMonths > 0 || summaryWith.interestSavings > 0.0

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.large)
    ) {
        ResultHeader(
            value = summaryWith.interestSavings.toCurrencyBR()
        )

        AppCard {
            Text(
                text = SimulationTexts.summaryWithoutTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(AppSpacing.small))
            SummaryRows(summaryWithout)

            Divider(
                modifier = Modifier.padding(vertical = AppSpacing.medium),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Text(
                text = SimulationTexts.summaryWithTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(AppSpacing.small))
            SummaryRows(summaryWith)

            if (showSavings) {
                Divider(
                    modifier = Modifier.padding(vertical = AppSpacing.medium),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Text(
                    text = SimulationTexts.savingsTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(AppSpacing.small))
                AppFinancialInfoRow(
                    label = SimulationTexts.savingsInterestLabel,
                    value = summaryWith.interestSavings.toCurrencyBR()
                )
                AppFinancialInfoRow(
                    label = SimulationTexts.savingsTermLabel,
                    value = summaryWith.reducedMonths.formatTerms()
                )
            }
        }

        Spacer(Modifier.height(AppSpacing.large))

        AppButton(
            text = SimulationTexts.viewTableButton,
            onClick = onViewDetails
        )

        AppButton(
            text = SimulationTexts.editSimulationButton,
            onClick = onEditSimulation,
            variant = com.elab.amortizaplus.presentation.ds.components.ButtonVariant.Secondary
        )

        AppButton(
            text = SimulationTexts.newSimulationButton,
            onClick = onNewSimulation,
            variant = com.elab.amortizaplus.presentation.ds.components.ButtonVariant.Text
        )
    }
}

@Composable
private fun ResultHeader(value: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        Text(
            text = SimulationTexts.resultSectionTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = SimulationTexts.savingsTitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SummaryRows(summary: SimulationSummary) {
    val systemLabel = summary.system.toLabel()
    AppFinancialInfoRow(
        label = SimulationTexts.systemLabel,
        value = systemLabel
    )
    AppFinancialInfoRow(
        label = SimulationTexts.totalPaidLabel,
        value = summary.totalPaid.toCurrencyBR()
    )
    AppFinancialInfoRow(
        label = SimulationTexts.totalInterestLabel,
        value = summary.totalInterest.toCurrencyBR()
    )
    AppFinancialInfoRow(
        label = SimulationTexts.termLabel,
        value = summary.totalMonths.formatTerms()
    )
}

private fun AmortizationSystem?.toLabel(): String = when (this) {
    AmortizationSystem.SAC -> SimulationTexts.systemSac
    AmortizationSystem.PRICE -> SimulationTexts.systemPrice
    null -> SimulationTexts.notAvailable
}
