package com.elab.amortizaplus.presentation.ds.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.elab.amortizaplus.presentation.ds.foundation.AppDimens
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing

/**
 *
 * Card padronizado do Design System.
 * Aplica elevação, cor de fundo e espaçamento consistentes.
 * Ideal para agrupar informações relacionadas (resumos, parcelas, estatísticas).
 *
 * @param modifier Modificador Compose
 * @param containerColor Cor de fundo do card (padrão: surface)
 * @param contentColor Cor do conteúdo (padrão: onSurface)
 * @param content Conteúdo do card
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor =  contentColor,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppDimens.elevationMedium
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.medium),
            content = content
        )
    }
}

/**
 *
 * Card para exibir informações de resumo financeiro.
 * Aplica cor de container primário para destacar informações importantes.
 *
 * @param modifier Modificador Compose
 * @param content Conteúdo do card
 */
@Composable
fun AppSummaryCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        content = content
    )
}

/**
 *
 * Card para exibir informações de economia/ganhos.
 * Aplica cor terciária para destacar benefícios obtidos.
 *
 * @param modifier Modificador Compose
 * @param content Conteúdo do card
 */
@Composable
fun AppSuccessCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        content = content
    )
}

/**
 *
 * Card para exibir avisos ou alertas.
 * Aplica cor secundária para informações de contexto.
 *
 * @param modifier Modificador Compose
 * @param content Conteúdo do card
 */
@Composable
fun AppInfoCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        content = content
    )
}
