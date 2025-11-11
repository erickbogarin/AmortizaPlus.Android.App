package com.elab.amortizaplus.presentation.ds.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.elab.amortizaplus.presentation.ds.foundation.AppColors
import com.elab.amortizaplus.presentation.ds.foundation.AppAnimationDefaults
import com.elab.amortizaplus.presentation.ds.foundation.AppDimens
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing

/**
 *
 * Campo de texto padronizado do Design System com feedback visual melhorado.
 * Melhorias de UX:
 * - Ícone de erro/sucesso animado
 * - Transição suave de mensagens
 * - Estados visuais claros
 * - Microinterações padronizadas
 *
 * IMPORTANTE: 100% baseado em tokens (zero hardcodes).
 *
 * @param value Texto atual do campo
 * @param onValueChange Callback ao alterar o texto
 * @param label Rótulo do campo
 * @param modifier Modificador Compose
 * @param placeholder Texto de dica (opcional)
 * @param leadingIcon Ícone à esquerda (opcional)
 * @param supportingText Texto de ajuda/erro abaixo do campo (opcional)
 * @param isError Indica estado de erro
 * @param showSuccessIcon Exibe ícone de sucesso quando válido
 * @param enabled Habilita/desabilita o campo
 * @param readOnly Campo apenas leitura
 * @param singleLine Força linha única
 * @param maxLines Máximo de linhas
 * @param keyboardOptions Opções de teclado
 * @param keyboardActions Ações do teclado
 * @param visualTransformation Transformação visual (ex: máscara)
 */
@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    showSuccessIcon: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            leadingIcon = leadingIcon,
            trailingIcon = {
                // Ícone de feedback visual com animação padronizada
                AnimatedVisibility(
                    visible = isError || (showSuccessIcon && value.isNotBlank()),
                    enter = AppAnimationDefaults.defaultEnter(),
                    exit = AppAnimationDefaults.defaultExit()
                ) {
                    Icon(
                        imageVector = if (isError) Icons.Default.Close else
                            Icons.Default.CheckCircle,
                        contentDescription = if (isError) "Campo inválido" else
                            "Campo válido",
                        tint = if (isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            AppColors.Success // ← Token semântico
                        },
                        modifier = Modifier.size(AppDimens.iconSizeMedium)
                    )
                }
            },
            isError = isError,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )
        // Mensagem de suporte animada (zero hardcodes)
        AnimatedVisibility(
            visible = supportingText != null,
            enter = AppAnimationDefaults.defaultEnter(),
            exit = AppAnimationDefaults.defaultExit()
        ) {
            supportingText?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(AppSpacing.small)) // ← Token
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}
