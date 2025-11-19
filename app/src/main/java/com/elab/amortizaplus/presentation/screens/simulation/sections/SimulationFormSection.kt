package com.elab.amortizaplus.presentation.screens.simulation.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.InterestRateType
import com.elab.amortizaplus.presentation.designsystem.theme.AmortizaPlusTheme
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.AppCard
import com.elab.amortizaplus.presentation.ds.components.AppOptionChip
import com.elab.amortizaplus.presentation.ds.components.AppOutlinedTextField
import com.elab.amortizaplus.presentation.ds.components.textfield.TextFieldVariant
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.SimulationFormActions
import com.elab.amortizaplus.presentation.screens.simulation.SimulationFormState
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import com.elab.amortizaplus.presentation.screens.simulation.*
//import com.elab.amortizaplus.presentation.util.asString
//import com.elab.amortizaplus.presentation.util.text.SimulationTexts

/**
 * Seção do formulário de entrada de dados da simulação.
 *
 * Totalmente aderente ao Design System e UiText Providers.
 */
@Composable
fun SimulationFormSection(
    formState: SimulationFormState,
    actions: SimulationFormActions,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {

        Text(
            text = SimulationTexts.formSectionTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(AppSpacing.medium))

        // Valor do empréstimo
        AppOutlinedTextField(
            value = formState.loanAmount,
            onValueChange = actions.onLoanAmountChange,
            label = SimulationTexts.loanAmountLabel,
            placeholder = SimulationTexts.loanAmountPlaceholder,
            variant = TextFieldVariant.Money,
            supportingText = formState.loanAmountError,
            isError = formState.loanAmountError != null,
            showSuccessIcon = formState.loanAmountError == null && formState.loanAmount.isNotBlank()
        )

        Spacer(Modifier.height(AppSpacing.small))

        // Taxa de juros
        AppOutlinedTextField(
            value = formState.interestRate,
            onValueChange = actions.onInterestRateChange,
            label = SimulationTexts.interestRateLabel,
            placeholder = SimulationTexts.interestRatePlaceholder,
            variant = TextFieldVariant.Percentage,
            supportingText = formState.interestRateError,
            isError = formState.interestRateError != null,
            showSuccessIcon = formState.interestRateError == null && formState.interestRate.isNotBlank()
        )

        Spacer(Modifier.height(AppSpacing.small))

        // Tipo de taxa (Anual / Mensal)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            AppOptionChip(
                selected = formState.rateType == InterestRateType.ANNUAL,
                text = SimulationTexts.rateTypeAnnual,
                onClick = { actions.onRateTypeChange(InterestRateType.ANNUAL) },
                modifier = Modifier.weight(1f)
            )
            AppOptionChip(
                selected = formState.rateType == InterestRateType.MONTHLY,
                text = SimulationTexts.rateTypeMonthly,
                onClick = { actions.onRateTypeChange(InterestRateType.MONTHLY) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(AppSpacing.small))

        // Prazo (meses)
        AppOutlinedTextField(
            value = formState.terms,
            onValueChange = actions.onTermsChange,
            label = SimulationTexts.termsLabel,
            placeholder = SimulationTexts.termsPlaceholder,
            
            variant = TextFieldVariant.Number,
            supportingText = formState.termsError,
            isError = formState.termsError != null,
            showSuccessIcon = formState.termsError == null && formState.terms.isNotBlank()
        )

        Spacer(Modifier.height(AppSpacing.small))

        // Sistema de amortização (SAC / PRICE)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            AppOptionChip(
                selected = formState.system == AmortizationSystem.SAC,
                text = SimulationTexts.systemSac,
                onClick = { actions.onSystemChange(AmortizationSystem.SAC) },
                modifier = Modifier.weight(1f)
            )
            AppOptionChip(
                selected = formState.system == AmortizationSystem.PRICE,
                text = SimulationTexts.systemPrice,
                onClick = { actions.onSystemChange(AmortizationSystem.PRICE) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(AppSpacing.medium))

        // Botão calcular
        AppButton(
            text = SimulationTexts.calculateButton,
            onClick = actions.onCalculate,
            enabled = formState.isValid() && !isLoading,
            isLoading = isLoading
        )
    }
}


// =============================================================================
// PREVIEW
// =============================================================================

@Preview(showBackground = true)
@Composable
private fun PreviewSimulationFormSection() {
    AmortizaPlusTheme() {
        SimulationFormSection(
            formState = SimulationFormState(
                loanAmount = "150000",
                interestRate = "13",
                terms = "420"
            ),
            actions = SimulationFormActions(
                onLoanAmountChange = {},
                onInterestRateChange = {},
                onTermsChange = {},
                onRateTypeChange = {},
                onSystemChange = {},
                onCalculate = {}
            ),
            isLoading = false
        )
    }
}