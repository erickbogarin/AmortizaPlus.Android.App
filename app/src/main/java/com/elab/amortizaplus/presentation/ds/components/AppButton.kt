package com.elab.amortizaplus.presentation.ds.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elab.amortizaplus.presentation.ds.foundation.AppDimens
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing

/**
 * Botão padronizado do Design System.
 *
 * Variantes:
 * - Primary: ação principal (fundo colorido)
 * - Secondary: ação secundária (outlined)
 * - Text: ação terciária (sem borda)
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: @Composable (() -> Unit)? = null
) {
    when(variant) {
        ButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                enabled = enabled && !isLoading,
                modifier = modifier
                    .fillMaxWidth()
                    .height(AppDimens.buttonHeightMedium),
                contentPadding = PaddingValues(horizontal = AppSpacing.medium),
                content = { ButtonContent(text, isLoading, icon) }
            )
        }

        ButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled && !isLoading,
                modifier = modifier
                    .fillMaxWidth()
                    .height(AppDimens.buttonHeightMedium),
                content = { ButtonContent (text, isLoading, icon) }
            )
        }

        ButtonVariant.Text -> {
            TextButton(
                onClick = onClick,
                enabled = enabled && !isLoading,
                content = { ButtonContent (text, isLoading, icon) }
            )
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    isLoading: Boolean,
    icon: @Composable (() -> Unit)?
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(AppDimens.iconSizeMedium),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.width(AppSpacing.small))
    }
    icon?.let {
        it()
        Spacer(modifier = Modifier.width(AppSpacing.small))
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge
    )
}

enum class ButtonVariant {
    Primary,
    Secondary,
    Text
}