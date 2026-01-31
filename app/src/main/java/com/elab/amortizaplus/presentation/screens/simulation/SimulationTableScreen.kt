package com.elab.amortizaplus.presentation.screens.simulation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elab.amortizaplus.domain.model.Installment
import com.elab.amortizaplus.presentation.ds.components.AppButton
import com.elab.amortizaplus.presentation.ds.components.ButtonVariant
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.util.toCurrencyBR

@Composable
fun SimulationTableScreen(
    installmentsWithout: List<Installment>,
    installmentsWith: List<Installment>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showWithExtra by rememberSaveable { mutableStateOf(true) }
    val selectedColumnsState = remember {
        mutableStateOf(
            setOf(
                TableColumn.Amortization,
                TableColumn.Interest,
                TableColumn.Installment,
                TableColumn.ExtraAmortization,
                TableColumn.RemainingBalance
            )
        )
    }
    val selectedColumns = selectedColumnsState.value
    val data = if (showWithExtra) installmentsWith else installmentsWithout

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        Text(
            text = "Tabela Detalhada",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showWithExtra) {
                    "Mostrando: com amortização extra"
                } else {
                    "Mostrando: sem amortização extra"
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Com extra",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.width(AppSpacing.small))
                Switch(
                    checked = showWithExtra,
                    onCheckedChange = { showWithExtra = it }
                )
            }
        }

        ColumnSelector(
            selected = selectedColumns,
            onToggle = { column ->
                selectedColumnsState.value = selectedColumns.toggle(column)
            }
        )

        TableContent(
            data = data,
            selectedColumns = selectedColumns
        )

        Spacer(Modifier.height(AppSpacing.medium))

        AppButton(
            text = "Voltar",
            onClick = onBack,
            variant = ButtonVariant.Secondary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private enum class TableColumn(
    val label: String,
    val width: Dp
) {
    Amortization("Amortização", 140.dp),
    Interest("Juros", 120.dp),
    Installment("Parcela", 140.dp),
    ExtraAmortization("Extra", 120.dp),
    RemainingBalance("Saldo", 160.dp)
}

private fun Set<TableColumn>.toggle(column: TableColumn): Set<TableColumn> =
    if (contains(column)) this - column else this + column

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnSelector(
    selected: Set<TableColumn>,
    onToggle: (TableColumn) -> Unit
) {
    Column {
        Text(
            text = "Colunas",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(AppSpacing.small))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            TableColumn.values().forEach { column ->
                FilterChip(
                    selected = selected.contains(column),
                    onClick = { onToggle(column) },
                    label = { Text(column.label) }
                )
            }
        }
    }
}

@Composable
private fun TableContent(
    data: List<Installment>,
    selectedColumns: Set<TableColumn>
) {
    val scrollState = rememberScrollState()
    val headerColumns = TableColumn.values().filter { selectedColumns.contains(it) }
    val tableWidth = headerColumns.fold(80.dp) { acc, column -> acc + column.width }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        Column(modifier = Modifier.width(tableWidth)) {
            TableHeaderRow(columns = headerColumns)
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(data) { item ->
                    TableDataRow(item = item, columns = headerColumns)
                }
                item {
                    TableFooterRow(data = data, columns = headerColumns)
                }
            }
        }
    }
}

@Composable
private fun TableHeaderRow(columns: List<TableColumn>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        TableCell(text = "Mês", width = 80.dp, bold = true)
        columns.forEach { column ->
            TableCell(text = column.label, width = column.width, bold = true)
        }
    }
}

@Composable
private fun TableDataRow(
    item: Installment,
    columns: List<TableColumn>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        TableCell(text = item.month.toString(), width = 80.dp)
        columns.forEach { column ->
            TableCell(
                text = column.valueFor(item),
                width = column.width
            )
        }
    }
}

@Composable
private fun TableFooterRow(
    data: List<Installment>,
    columns: List<TableColumn>
) {
    val totals = TableTotals.from(data)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AppSpacing.small),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        TableCell(text = "Totais", width = 80.dp, bold = true)
        columns.forEach { column ->
            TableCell(
                text = totals.valueFor(column),
                width = column.width,
                bold = true
            )
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    width: Dp,
    bold: Boolean = false
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
        modifier = Modifier.width(width)
    )
}

private fun TableColumn.valueFor(item: Installment): String = when (this) {
    TableColumn.Amortization -> item.amortization.toCurrencyBR()
    TableColumn.Interest -> item.interest.toCurrencyBR()
    TableColumn.Installment -> item.installment.toCurrencyBR()
    TableColumn.ExtraAmortization -> item.extraAmortization.toCurrencyBR()
    TableColumn.RemainingBalance -> item.remainingBalance.toCurrencyBR()
}

private data class TableTotals(
    val amortization: Double,
    val interest: Double,
    val installment: Double,
    val extra: Double,
    val remaining: Double
) {
    companion object {
        fun from(data: List<Installment>): TableTotals {
            return TableTotals(
                amortization = data.sumOf { it.amortization },
                interest = data.sumOf { it.interest },
                installment = data.sumOf { it.installment },
                extra = data.sumOf { it.extraAmortization },
                remaining = data.lastOrNull()?.remainingBalance ?: 0.0
            )
        }
    }

    fun valueFor(column: TableColumn): String = when (column) {
        TableColumn.Amortization -> amortization.toCurrencyBR()
        TableColumn.Interest -> interest.toCurrencyBR()
        TableColumn.Installment -> installment.toCurrencyBR()
        TableColumn.ExtraAmortization -> extra.toCurrencyBR()
        TableColumn.RemainingBalance -> remaining.toCurrencyBR()
    }
}
