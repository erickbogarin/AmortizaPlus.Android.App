package com.elab.amortizaplus.presentation.screens.simulation.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt
import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.ExtraAmortizationStrategy
import com.elab.amortizaplus.domain.model.InterestRateType
import com.elab.amortizaplus.presentation.designsystem.theme.AmortizaPlusTheme
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.AppOptionChip
import com.elab.amortizaplus.presentation.ds.components.AppOutlinedTextField
import com.elab.amortizaplus.presentation.ds.components.ButtonVariant
import com.elab.amortizaplus.presentation.ds.components.textfield.TextFieldVariant
import com.elab.amortizaplus.presentation.ds.foundation.AppDimens
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.ExtraAmortizationFormItem
import com.elab.amortizaplus.presentation.screens.simulation.SimulationFormActions
import com.elab.amortizaplus.presentation.screens.simulation.SimulationFormState
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import com.elab.amortizaplus.presentation.screens.simulation.validation.SimulationInputValidator
import com.elab.amortizaplus.presentation.ds.components.textfield.formatters.PercentageFormatter
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
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.extraSmall)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = AppDimens.elevationSmall
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.small)
            ) {
                Text(
                    text = SimulationTexts.financingAboutTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(AppSpacing.extraSmallTight))

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

                Spacer(Modifier.height(AppSpacing.extraSmallTight))

                val termsValue = formState.terms.toIntOrNull()
                val minTerms = SimulationInputValidator.MIN_TERMS
                val maxTerms = SimulationInputValidator.MAX_TERMS
                val sliderValue = (termsValue ?: minTerms).coerceIn(minTerms, maxTerms).toFloat()

                ParameterHeader(
                    label = SimulationTexts.termsLabel,
                    value = "${sliderValue.roundToInt()} ${SimulationTexts.termsUnitMonths}"
                )

                Spacer(Modifier.height(AppSpacing.extraSmallTight))

                Slider(
                    value = sliderValue,
                    onValueChange = { value ->
                        actions.onTermsChange(value.roundToInt().toString())
                    },
                    valueRange = minTerms.toFloat()..maxTerms.toFloat()
                )

                if (formState.termsError != null) {
                    Text(
                        text = formState.termsError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(AppSpacing.extraSmallTight))

                // Data de início
                Column {
                    val startDateDisplay = formatMonthYear(formState.startDate)
                    ParameterHeader(
                        label = SimulationTexts.startDateLabel,
                        value = startDateDisplay
                    )

                    Spacer(Modifier.height(AppSpacing.extraSmall))

                    AppOutlinedTextField(
                        value = formState.startDate,
                        onValueChange = actions.onStartDateChange,
                        label = "",
                        placeholder = SimulationTexts.startDatePlaceholder,
                        variant = TextFieldVariant.MonthYear,
                        supportingText = formState.startDateError,
                        isError = formState.startDateError != null,
                        showSuccessIcon = formState.startDateError == null && formState.startDate.isNotBlank()
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = AppDimens.elevationSmall
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.small)
            ) {
                Text(
                    text = SimulationTexts.conditionsTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(AppSpacing.extraSmallTight))

                Column {
                    val rateDisplay = PercentageFormatter().formatForDisplay(formState.interestRate)
                    ParameterHeader(
                        label = SimulationTexts.interestRateLabel,
                        value = rateDisplay
                    )

                    Spacer(Modifier.height(AppSpacing.extraSmall))

                    AppOutlinedTextField(
                        value = formState.interestRate,
                        onValueChange = actions.onInterestRateChange,
                        label = "",
                        placeholder = SimulationTexts.interestRatePlaceholder,
                        variant = TextFieldVariant.Percentage,
                        supportingText = formState.interestRateError,
                        isError = formState.interestRateError != null,
                        showSuccessIcon = formState.interestRateError == null && formState.interestRate.isNotBlank()
                    )

                    Spacer(Modifier.height(AppSpacing.extraSmallTight))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.extraSmallTight)
                    ) {
                        AppOptionChip(
                            selected = formState.rateType == InterestRateType.MONTHLY,
                            text = SimulationTexts.rateTypeMonthly,
                            onClick = { actions.onRateTypeChange(InterestRateType.MONTHLY) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = AppDimens.buttonHeightMedium)
                        )
                        AppOptionChip(
                            selected = formState.rateType == InterestRateType.ANNUAL,
                            text = SimulationTexts.rateTypeAnnual,
                            onClick = { actions.onRateTypeChange(InterestRateType.ANNUAL) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = AppDimens.buttonHeightMedium)
                        )
                    }
                }

                Spacer(Modifier.height(AppSpacing.extraSmallTight))

                ParameterHeader(
                    label = SimulationTexts.amortizationTypeTitle,
                    value = if (formState.system == AmortizationSystem.SAC) {
                        SimulationTexts.systemSac
                    } else {
                        SimulationTexts.systemPrice
                    }
                )

                Spacer(Modifier.height(AppSpacing.extraSmallTight))

                // Sistema de amortização (SAC / PRICE)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.extraSmallTight)
                ) {
                    AppOptionChip(
                        selected = formState.system == AmortizationSystem.SAC,
                        text = SimulationTexts.systemSac,
                        onClick = { actions.onSystemChange(AmortizationSystem.SAC) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = AppDimens.buttonHeightMedium)
                    )
                    AppOptionChip(
                        selected = formState.system == AmortizationSystem.PRICE,
                        text = SimulationTexts.systemPrice,
                        onClick = { actions.onSystemChange(AmortizationSystem.PRICE) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = AppDimens.buttonHeightMedium)
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = AppDimens.elevationSmall
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.small)
            ) {
                Text(
                    text = SimulationTexts.extraAmortizationsTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(AppSpacing.extraSmallTight))

                ExtraAmortizationsSection(
                    items = formState.extraAmortizations,
                    onAdd = actions.onAddExtraAmortization,
                    onRemove = actions.onRemoveExtraAmortization,
                    onMonthChange = actions.onExtraMonthChange,
                    onAmountChange = actions.onExtraAmountChange,
                    onStrategyChange = actions.onExtraStrategyChange
                )
            }
        }

    }
}

@Composable
private fun ParameterHeader(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppDimens.buttonHeightSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
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
    if (items.isEmpty()) {
        Text(
            text = SimulationTexts.extraAmortizationsEmpty,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(AppSpacing.extraSmallTight))
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

private fun formatMonthYear(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    return when {
        digits.isBlank() -> ""
        digits.length <= 2 -> digits
        else -> digits.substring(0, 2) + "/" + digits.substring(2)
    }
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
