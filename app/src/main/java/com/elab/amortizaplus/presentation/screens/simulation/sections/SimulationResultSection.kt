package com.elab.amortizaplus.presentation.screens.simulation.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.SimulationSummary
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.AppFinancialInfoRow
import com.elab.amortizaplus.presentation.ds.components.AppSummaryCard
import com.elab.amortizaplus.presentation.ds.components.AppSuccessCard
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import com.elab.amortizaplus.presentation.util.formatTerms
import com.elab.amortizaplus.presentation.util.toCurrencyBR

@Composable
fun SimulationResultSection(
    summaryWithout: SimulationSummary,
    summaryWith: SimulationSummary,
    modifier: Modifier = Modifier,
    onViewDetails: () -> Unit = {}
) {
    val showSavings = summaryWith.reducedMonths > 0 || summaryWith.interestSavings > 0.0

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        Text(
            text = SimulationTexts.resultSectionTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        SummaryWithoutExtraCard(summaryWithout)

        if (showSavings) {
            SummaryWithExtraCard(summaryWith)
            SavingsCard(summaryWith)
        }

        AppButton(
            text = SimulationTexts.viewTableButton,
            onClick = onViewDetails
        )
    }
}

@Composable
private fun SummaryWithoutExtraCard(summary: SimulationSummary) {
    val systemLabel = summary.system.toLabel()
    AppSummaryCard {
        Text(
            text = SimulationTexts.summaryWithoutTitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(AppSpacing.small))

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
}

@Composable
private fun SummaryWithExtraCard(summary: SimulationSummary) {
    val systemLabel = summary.system.toLabel()
    AppSuccessCard {
        Text(
            text = SimulationTexts.summaryWithTitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(AppSpacing.small))

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
}

@Composable
private fun SavingsCard(summary: SimulationSummary) {
    AppSuccessCard {
        Text(
            text = SimulationTexts.savingsTitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(AppSpacing.small))

        AppFinancialInfoRow(
            label = SimulationTexts.savingsInterestLabel,
            value = summary.interestSavings.toCurrencyBR()
        )
        AppFinancialInfoRow(
            label = SimulationTexts.savingsTermLabel,
            value = summary.reducedMonths.formatTerms()
        )
    }
}

private fun AmortizationSystem?.toLabel(): String = when (this) {
    AmortizationSystem.SAC -> SimulationTexts.systemSac
    AmortizationSystem.PRICE -> SimulationTexts.systemPrice
    null -> SimulationTexts.notAvailable
}
