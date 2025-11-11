package com.elab.amortizaplus.presentation.ds.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

/**
 *
 * Componente para exibir pares label-valor de informações financeiras.
 * Layout padrão: label à esquerda, valor à direita.
 * Reutilizável em cards de resumo, parcelas e estatísticas.
 *
 * @param label Rótulo da informação (ex: "Total Pago")
 * @param value Valor da informação (ex: "R$ 381.737,55")
 * @param modifier Modificador Compose
 * @param valueFontWeight Peso da fonte do valor (padrão: Normal)
 */
@Composable
fun AppFinancialInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueFontWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement =  Arrangement.SpaceAround
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = valueFontWeight,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}