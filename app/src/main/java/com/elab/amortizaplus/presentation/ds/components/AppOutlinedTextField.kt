package com.elab.amortizaplus.presentation.ds.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

import com.elab.amortizaplus.presentation.ds.components.textfield.TextFieldVariant
import com.elab.amortizaplus.presentation.ds.components.textfield.toConfig
import com.elab.amortizaplus.presentation.ds.foundation.AppColors
import com.elab.amortizaplus.presentation.ds.foundation.AppDimens
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing

/**
 * TextField padronizado do AmortizaPlus com suporte a formatação.
 *
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * ✅ CONTRATO FUNDAMENTAL (leia antes de modificar):
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 * 1. `value` prop SEMPRE representa valor RAW (sem formatação)
 *    - Exemplo Money: "12345" (centavos, não "R$ 123,45")
 *    - Exemplo Percentage: "1250" (basis points, não "12,50%")
 *    - Exemplo CPF: "12345678900" (dígitos, não "123.456.789-00")
 *
 * 2. `displayValue` é calculado via `formatForDisplay(value)`
 *    - ÚNICA fonte de formatação visual
 *    - Transformations NÃO devem duplicar esta lógica
 *
 * 3. VisualTransformation gerencia APENAS cursor
 *    - SimpleCursorTransformation: cursor no final (Money, %, Number)
 *    - MaskTransformation: cursor inteligente (CPF, Phone, CEP)
 *
 * 4. `onValueChange` SEMPRE emite valor RAW
 *    - Sanitizado (caracteres inválidos removidos)
 *    - Parseado (convertido para formato interno)
 *    - SEM formatação visual
 *
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 * Arquitetura (ATUALIZADA):
 * 1. ViewModel emite valores "crus" (raw) - sem formatação
 * 2. formatForDisplay() formata para exibição visual
 * 3. VisualTransformation APENAS gerencia cursor
 * 4. onValueChange emite valores crus de volta para ViewModel
 *
 * Fluxo de dados (ATUALIZADO):
 * ┌──────────────────────────────────────────────────┐
 * │ ViewModel                                         │
 * │   state: "12345" (centavos)                      │
 * └────────────┬─────────────────────────────────────┘
 *              │ (value prop)
 *              ▼
 * ┌──────────────────────────────────────────────────┐
 * │ TextField                                         │
 * │   formatForDisplay("12345") → "R$ 123,45"        │
 * │   visualTransformation → gerencia cursor         │
 * └────────────┬─────────────────────────────────────┘
 *              │ (onValueChange)
 *              ▼
 * ┌──────────────────────────────────────────────────┐
 * │ User types: "1234567"                            │
 * │   formatter.sanitize → "1234567"                 │
 * │   formatter.parse → "1234567"                    │
 * │   emits: "1234567" (raw)                         │
 * └────────────┬─────────────────────────────────────┘
 *              │
 *              ▼
 * ┌──────────────────────────────────────────────────┐
 * │ ViewModel                                         │
 * │   state = "1234567" (centavos = R$ 12.345,67)   │
 * └──────────────────────────────────────────────────┘
 *
 * @param value Valor cru (raw) do ViewModel - SEM formatação
 * @param onValueChange Callback que emite valor cru de volta
 * @param label Rótulo do campo
 * @param variant Tipo de formatação (Default, Money, Percentage, etc)
 * @param placeholder Texto de exemplo
 * @param supportingText Texto de ajuda ou erro abaixo do campo
 * @param isError Se o campo está em estado de erro
 * @param showSuccessIcon Se deve mostrar ícone de sucesso quando preenchido
 * @param enabled Se o campo está habilitado
 * @param keyboardActions Ações do teclado (Done, Next, etc)
 * @param modifier Modificador Compose
 */
@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    variant: TextFieldVariant = TextFieldVariant.Default,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    showSuccessIcon: Boolean = false,
    enabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    modifier: Modifier = Modifier
) {
    val config = remember(variant) { variant.toConfig() }

    val actualValue = value  // SEMPRE raw value

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = actualValue,
            onValueChange = { newInput ->
                // 1. Sanitiza: remove caracteres inválidos
                val sanitized = config.formatter.sanitize(newInput)

                // 2. Parse: converte para valor cru
                val rawValue = config.formatter.parse(sanitized)

                // 3. Emite valor cru para ViewModel
                onValueChange(rawValue)
            },
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            visualTransformation = config.visualTransformation,
            singleLine = true,
            keyboardOptions = config.keyboard,
            keyboardActions = keyboardActions,
            isError = isError,
            enabled = enabled,
            trailingIcon = {
                TrailingIcon(
                    isError = isError,
                    showSuccess = showSuccessIcon && value.isNotBlank(),
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                // Customizações de cor podem ser adicionadas aqui
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Texto de suporte (erro ou informação)
        AnimatedVisibility(visible = supportingText != null) {
            SupportingText(
                text = supportingText.orEmpty(),
                isError = isError
            )
        }
    }
}

/**
 * Ícone de trailing (erro ou sucesso).
 */
@Composable
private fun TrailingIcon(
    isError: Boolean,
    showSuccess: Boolean
) {
    AnimatedVisibility(visible = isError || showSuccess) {
        Icon(
            imageVector = if (isError) {
                Icons.Default.Close
            } else {
                Icons.Default.CheckCircle
            },
            contentDescription = if (isError) {
                "Campo inválido"
            } else {
                "Campo válido"
            },
            tint = if (isError) {
                MaterialTheme.colorScheme.error
            } else {
                AppColors.Success
            },
            modifier = Modifier.size(AppDimens.iconSizeMedium)
        )
    }
}

/**
 * Texto de suporte (abaixo do campo).
 */
@Composable
private fun SupportingText(
    text: String,
    isError: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = AppSpacing.small, top = AppSpacing.extraSmall)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isError) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

// ========================================
// PREVIEW (para desenvolvimento)
// ========================================

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun AppOutlinedTextFieldPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            // Campo de texto padrão
            var textValue by remember { mutableStateOf("") }
            AppOutlinedTextField(
                value = textValue,
                onValueChange = { textValue = it },
                label = "Nome completo",
                variant = TextFieldVariant.Default,
                placeholder = "Digite seu nome"
            )

            // Campo numérico
            var numberValue by remember { mutableStateOf("") }
            AppOutlinedTextField(
                value = numberValue,
                onValueChange = { numberValue = it },
                label = "Prazo (meses)",
                variant = TextFieldVariant.Number,
                placeholder = "360"
            )

            // Campo monetário
            var moneyValue by remember { mutableStateOf("") }
            AppOutlinedTextField(
                value = moneyValue,
                onValueChange = { moneyValue = it },
                label = "Valor financiado",
                variant = TextFieldVariant.Money,
                placeholder = "0,00",
                supportingText = "Digite o valor que deseja financiar"
            )

            // Campo de porcentagem
            var percentValue by remember { mutableStateOf("") }
            AppOutlinedTextField(
                value = percentValue,
                onValueChange = { percentValue = it },
                label = "Taxa de juros",
                variant = TextFieldVariant.Percentage,
                placeholder = "0,00",
                showSuccessIcon = true
            )
        }
    }
}