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
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
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
import com.elab.amortizaplus.presentation.ds.components.AppCard
import com.elab.amortizaplus.presentation.ds.components.AppFinancialInfoRow
import com.elab.amortizaplus.presentation.ds.components.ButtonVariant
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import com.elab.amortizaplus.presentation.util.formatTerms
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
            text = SimulationTexts.tableTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = SimulationTexts.tableSubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.extraSmall)
        ) {
            Text(
                text = if (showWithExtra) {
                    SimulationTexts.tableShowingWithExtra
                } else {
                    SimulationTexts.tableShowingWithoutExtra
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
            ) {
                Text(
                    text = SimulationTexts.tableToggleLabel,
                    style = MaterialTheme.typography.bodySmall
                )
                Switch(
                    checked = showWithExtra,
                    onCheckedChange = { showWithExtra = it }
                )
            }
        }

        TableSummaryCard(data = data)

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
        text = SimulationTexts.tableBackButton,
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
    Amortization(SimulationTexts.tableColumnAmortization, 140.dp),
    Interest(SimulationTexts.tableColumnInterest, 120.dp),
    Installment(SimulationTexts.tableColumnInstallment, 140.dp),
    ExtraAmortization(SimulationTexts.tableColumnExtra, 120.dp),
    RemainingBalance(SimulationTexts.tableColumnBalance, 160.dp)
}

private fun Set<TableColumn>.toggle(column: TableColumn): Set<TableColumn> =
    if (contains(column) && size > 1) this - column else this + column

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnSelector(
    selected: Set<TableColumn>,
    onToggle: (TableColumn) -> Unit
) {
    Column {
        Text(
            text = SimulationTexts.tableColumnsTitle,
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

    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            Column(modifier = Modifier.width(tableWidth)) {
                TableHeaderRow(columns = headerColumns)
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(data) { item ->
                        TableDataRow(item = item, columns = headerColumns)
                    }
                    item {
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        TableFooterRow(data = data, columns = headerColumns)
                    }
                }
            }
        }
    }
}

@Composable
private fun TableSummaryCard(data: List<Installment>) {
    val totals = TableTotals.from(data)
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = SimulationTexts.tableSummaryTitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(AppSpacing.extraSmall))
        AppFinancialInfoRow(
            label = SimulationTexts.tableSummaryTotalPaid,
            value = totals.totalPaid.toCurrencyBR()
        )
        AppFinancialInfoRow(
            label = SimulationTexts.tableSummaryTotalInterest,
            value = totals.interest.toCurrencyBR()
        )
        AppFinancialInfoRow(
            label = SimulationTexts.tableSummaryTotalAmortized,
            value = totals.totalAmortized.toCurrencyBR()
        )
        AppFinancialInfoRow(
            label = SimulationTexts.tableSummaryTerm,
            value = totals.months.formatTerms()
        )
    }
}

@Composable
private fun TableHeaderRow(columns: List<TableColumn>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = AppSpacing.small, vertical = AppSpacing.small),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        TableCell(
            text = SimulationTexts.tableMonthHeader,
            width = 80.dp,
            bold = true,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textStyle = MaterialTheme.typography.labelLarge
        )
        columns.forEach { column ->
            TableCell(
                text = column.label,
                width = column.width,
                bold = true,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textStyle = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun TableDataRow(
    item: Installment,
    columns: List<TableColumn>
) {
    val hasExtra = item.extraAmortization > 0.0
    val rowBackground = when {
        hasExtra -> MaterialTheme.colorScheme.tertiaryContainer
        item.month % 2 == 0 -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val rowTextColor = if (hasExtra) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBackground)
            .padding(horizontal = AppSpacing.small, vertical = AppSpacing.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        TableCell(
            text = item.month.toString(),
            width = 80.dp,
            bold = hasExtra,
            color = rowTextColor
        )
        columns.forEach { column ->
            TableCell(
                text = column.valueFor(item),
                width = column.width,
                bold = hasExtra,
                color = rowTextColor
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
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = AppSpacing.small, vertical = AppSpacing.small),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        TableCell(
            text = SimulationTexts.tableTotalsLabel,
            width = 80.dp,
            bold = true,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textStyle = MaterialTheme.typography.labelLarge
        )
        columns.forEach { column ->
            TableCell(
                text = totals.valueFor(column),
                width = column.width,
                bold = true,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textStyle = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    width: Dp,
    bold: Boolean = false,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodySmall
) {
    Text(
        text = text,
        style = textStyle,
        fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
        color = color,
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
    val months: Int,
    val totalPaid: Double,
    val totalAmortized: Double,
    val amortization: Double,
    val interest: Double,
    val installment: Double,
    val extra: Double,
    val remaining: Double
) {
    companion object {
        fun from(data: List<Installment>): TableTotals {
            val amortization = data.sumOf { it.amortization }
            val extra = data.sumOf { it.extraAmortization }
            val installment = data.sumOf { it.installment }
            return TableTotals(
                months = data.size,
                totalPaid = installment + extra,
                totalAmortized = amortization + extra,
                amortization = amortization,
                interest = data.sumOf { it.interest },
                installment = installment,
                extra = extra,
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
