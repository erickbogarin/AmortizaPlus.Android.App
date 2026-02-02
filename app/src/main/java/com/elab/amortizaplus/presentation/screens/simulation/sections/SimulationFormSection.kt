package com.elab.amortizaplus.presentation.screens.simulation.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.ExtraAmortizationStrategy
import com.elab.amortizaplus.domain.model.InterestRateType
import com.elab.amortizaplus.presentation.designsystem.theme.AmortizaPlusTheme
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.AppCard
import com.elab.amortizaplus.presentation.ds.components.AppOptionChip
import com.elab.amortizaplus.presentation.ds.components.AppOutlinedTextField
import com.elab.amortizaplus.presentation.ds.components.ButtonVariant
import com.elab.amortizaplus.presentation.ds.components.textfield.TextFieldVariant
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.ExtraAmortizationFormItem
import com.elab.amortizaplus.presentation.screens.simulation.SimulationFormActions
import com.elab.amortizaplus.presentation.screens.simulation.SimulationFormState
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
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

        // Data de início
        AppOutlinedTextField(
            value = formState.startDate,
            onValueChange = actions.onStartDateChange,
            label = SimulationTexts.startDateLabel,
            placeholder = SimulationTexts.startDatePlaceholder,
            variant = TextFieldVariant.MonthYear,
            supportingText = formState.startDateError,
            isError = formState.startDateError != null,
            showSuccessIcon = formState.startDateError == null && formState.startDate.isNotBlank()
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

        ExtraAmortizationsSection(
            items = formState.extraAmortizations,
            onAdd = actions.onAddExtraAmortization,
            onRemove = actions.onRemoveExtraAmortization,
            onMonthChange = actions.onExtraMonthChange,
            onAmountChange = actions.onExtraAmountChange,
            onStrategyChange = actions.onExtraStrategyChange
        )

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

@Composable
private fun ExtraAmortizationsSection(
    items: List<ExtraAmortizationFormItem>,
    onAdd: () -> Unit,
    onRemove: (Long) -> Unit,
    onMonthChange: (Long, String) -> Unit,
    onAmountChange: (Long, String) -> Unit,
    onStrategyChange: (Long, ExtraAmortizationStrategy) -> Unit
) {
    Text(
        text = SimulationTexts.extraAmortizationsTitle,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(Modifier.height(AppSpacing.small))

    if (items.isEmpty()) {
        Text(
            text = SimulationTexts.extraAmortizationsEmpty,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(AppSpacing.small))
    }

    items.forEach { item ->
        ExtraAmortizationItem(
            item = item,
            onMonthChange = onMonthChange,
            onAmountChange = onAmountChange,
            onStrategyChange = onStrategyChange,
            onRemove = onRemove
        )
        Spacer(Modifier.height(AppSpacing.small))
    }

    AppButton(
        text = SimulationTexts.extraAmortizationsAddButton,
        onClick = onAdd,
        variant = ButtonVariant.Secondary
    )
}

@Composable
private fun ExtraAmortizationItem(
    item: ExtraAmortizationFormItem,
    onMonthChange: (Long, String) -> Unit,
    onAmountChange: (Long, String) -> Unit,
    onStrategyChange: (Long, ExtraAmortizationStrategy) -> Unit,
    onRemove: (Long) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            AppOutlinedTextField(
                value = item.month,
                onValueChange = { onMonthChange(item.id, it) },
                label = SimulationTexts.extraAmortizationMonthLabel,
                placeholder = SimulationTexts.extraAmortizationMonthPlaceholder,
                variant = TextFieldVariant.Number,
                supportingText = item.monthError,
                isError = item.monthError != null,
                showSuccessIcon = item.monthError == null && item.month.isNotBlank(),
                modifier = Modifier.weight(1f)
            )

            AppOutlinedTextField(
                value = item.amount,
                onValueChange = { onAmountChange(item.id, it) },
                label = SimulationTexts.extraAmortizationAmountLabel,
                placeholder = SimulationTexts.extraAmortizationAmountPlaceholder,
                variant = TextFieldVariant.Money,
                supportingText = item.amountError,
                isError = item.amountError != null,
                showSuccessIcon = item.amountError == null && item.amount.isNotBlank(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(AppSpacing.extraSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            AppOptionChip(
                selected = item.strategy == ExtraAmortizationStrategy.REDUCE_TERM,
                text = SimulationTexts.extraAmortizationReduceTerm,
                onClick = { onStrategyChange(item.id, ExtraAmortizationStrategy.REDUCE_TERM) },
                modifier = Modifier.weight(1f)
            )
            AppOptionChip(
                selected = item.strategy == ExtraAmortizationStrategy.REDUCE_PAYMENT,
                text = SimulationTexts.extraAmortizationReducePayment,
                onClick = { onStrategyChange(item.id, ExtraAmortizationStrategy.REDUCE_PAYMENT) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(AppSpacing.extraSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            AppButton(
                text = SimulationTexts.extraAmortizationsRemoveButton,
                onClick = { onRemove(item.id) },
                variant = ButtonVariant.Text
            )
        }
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
                onStartDateChange = {},
                onRateTypeChange = {},
                onSystemChange = {},
                onAddExtraAmortization = {},
                onRemoveExtraAmortization = {},
                onExtraMonthChange = { _, _ -> },
                onExtraAmountChange = { _, _ -> },
                onExtraStrategyChange = { _, _ -> },
                onCalculate = {}
            ),
            isLoading = false
        )
    }
}
