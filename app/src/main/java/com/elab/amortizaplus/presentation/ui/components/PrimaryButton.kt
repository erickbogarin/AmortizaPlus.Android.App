package com.elab.amortizaplus.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elab.amortizaplus.presentation.ui.theme.Dimens
import com.elab.amortizaplus.presentation.ui.theme.Spacing

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.buttonHeightMedium),
        contentPadding = PaddingValues(horizontal = Spacing.medium)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.iconSizeMedium),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(Spacing.small))
        }

        icon?.let {
            it()
            Spacer(modifier = Modifier.width(Spacing.small))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}