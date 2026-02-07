package com.elab.amortizaplus.presentation.screens.simulation.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.AppCard
import com.elab.amortizaplus.presentation.ds.components.AppCard
import com.elab.amortizaplus.presentation.ds.components.AppLoadingIndicator
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts

@Composable
fun SimulationInitialSection(
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Text(
            text = SimulationTexts.initialTitle,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(AppSpacing.extraSmall))
        Text(
            text = SimulationTexts.initialDescription,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SimulationLoadingSection(
    modifier: Modifier = Modifier
) {
    AppLoadingIndicator(
        message = SimulationTexts.loadingMessage,
        modifier = modifier
    )
}

@Composable
fun SimulationErrorSection(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    ) {
        Text(
            text = SimulationTexts.errorTitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(AppSpacing.extraSmall))
        Text(
            text = "${SimulationTexts.errorDescriptionPrefix} $message",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(AppSpacing.medium))
        AppButton(
            text = SimulationTexts.retryButton,
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
