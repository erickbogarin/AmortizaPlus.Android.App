package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStoreOwner
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.AppInfoCard
import com.elab.amortizaplus.presentation.ds.components.ButtonVariant
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import org.koin.androidx.compose.koinViewModel

@Composable
fun SimulationTableRoute(
    onBack: () -> Unit,
    parentEntry: ViewModelStoreOwner,
    viewModel: SimulationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
) {
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState as? SimulationUiState.Success

    if (state == null) {
        AppInfoCard {
            Text(
                text = SimulationTexts.tableMissingTitle,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(AppSpacing.small))
            Text(
                text = SimulationTexts.tableMissingMessage,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(AppSpacing.medium))
            AppButton(
                text = SimulationTexts.tableBackButton,
                onClick = onBack,
                variant = ButtonVariant.Secondary
            )
        }
    } else {
        SimulationTableScreen(
            installmentsWithout = state.installmentsWithout,
            installmentsWith = state.installmentsWith,
            onBack = onBack
        )
    }
}
