package com.elab.amortizaplus.presentation.ui.screens.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elab.amortizaplus.domain.calculator.FinancingCalculator
import com.elab.amortizaplus.domain.model.AmortizationSystem
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow


@Composable
fun SimulationPreviewScreen() {
    // --- Configurações base ---
    val loanAmount = 121_000.0
    val annualRate = 0.13
    val terms = 420
    val amortizationMonth = 8
    val extraAmortizationValue = 76_000.0
    val formatCurrency = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    val monthlyRate = (1 + annualRate).pow(1.0 / 12.0) - 1
    val calculator = FinancingCalculator()

    // --- Simulações ---
    val (summaryWithout, summaryWith) = calculator.compare(
        loanAmount = loanAmount,
        monthlyRate = monthlyRate,
        terms = terms,
        system = AmortizationSystem.SAC,
        extraAmortizations = mapOf(amortizationMonth to extraAmortizationValue),
        reduceTerm = true
    )

    val installmentsWithout = calculator.calculate(
        loanAmount = loanAmount,
        monthlyRate = monthlyRate,
        terms = terms,
        system = AmortizationSystem.SAC,
        reduceTerm = false
    )

    val installmentsWith = calculator.calculate(
        loanAmount = loanAmount,
        monthlyRate = monthlyRate,
        terms = terms,
        system = AmortizationSystem.SAC,
        extraAmortizations = mapOf(amortizationMonth to extraAmortizationValue),
        reduceTerm = true
    )

    println("FINANCING_LOG → Valor: R$ ${"%.2f".format(loanAmount)} | Taxa: ${(annualRate * 100)}% a.a.")
    println("FINANCING_LOG → Amortização extra: R$ ${"%.2f".format(extraAmortizationValue)} no mês $amortizationMonth")
    println("FINANCING_LOG → Sem amortização: ${installmentsWithout.size} parcelas")
    println("FINANCING_LOG → Com amortização: ${installmentsWith.size} parcelas")
// --- Exibição ---
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("💰 Simulação SAC (Preview)", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // 🔹 Resumo SEM amortização
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF))) {
            Column(Modifier.padding(12.dp)) {
                Text("📈 Resumo SEM amortização", fontWeight = FontWeight.Bold)
                Text("Sistema: SAC")
                Text("Total Pago: ${formatCurrency.format(summaryWithout.totalPaid)}")
                Text("Total de Juros: ${formatCurrency.format(summaryWithout.totalInterest)}")
                Text("Total Amortizado: ${formatCurrency.format(summaryWithout.totalAmortized)}")
                Text("Meses: ${summaryWithout.totalMonths}")
            }
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 Resumo COM amortização
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
            Column(Modifier.padding(12.dp)) {
                Text("📉 Resumo COM amortização", fontWeight = FontWeight.Bold)
                Text("Sistema: SAC")
                Text("Total Pago: ${formatCurrency.format(summaryWith.totalPaid)}")
                Text("Total de Juros: ${formatCurrency.format(summaryWith.totalInterest)}")
                Text("Total Amortizado: ${formatCurrency.format(summaryWith.totalAmortized)}")
                Text("Meses: ${summaryWith.totalMonths}")
            }
        }

        Spacer(Modifier.height(20.dp))

        // 🎯 Economia
        val interestSavings = summaryWithout.totalInterest - summaryWith.totalInterest
        val monthsSaved = summaryWithout.totalMonths - summaryWith.totalMonths

        Text("🎯 Economia de juros: ${formatCurrency.format(interestSavings)}", fontWeight = FontWeight.Bold)
        Text("⏳ Redução de prazo: $monthsSaved meses")

        Spacer(Modifier.height(20.dp))
// 📅 Primeiras parcelas (sem amortização)
        Text("📆 Primeiras parcelas (sem amortização)", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        installmentsWithout.take(12).forEach { item ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
            ) {
                Column(Modifier.padding(10.dp)) {
                    Text("Mês ${item.month}")
                    Text("Amortização: ${formatCurrency.format(item.amortization)}")
                    Text("Juros: ${formatCurrency.format(item.interest)}")
                    Text("Parcela: ${formatCurrency.format(item.installment)}")
                    Text("Saldo devedor: ${formatCurrency.format(item.remainingBalance)}")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // 📅 Primeiras parcelas (com amortização)
        Text("📅 Primeiras parcelas (com amortização)", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        installmentsWith.take(12).forEach { item ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1FFF4))
            ) {
                Column(Modifier.padding(10.dp)) {
                    Text("Mês ${item.month}")
                    Text("Amortização: ${formatCurrency.format(item.amortization)}")
                    Text("Juros: ${formatCurrency.format(item.interest)}")
                    Text("Parcela: ${formatCurrency.format(item.installment)}")
                    if (item.extraAmortization > 0) {
                        Text("💰 Extra: ${formatCurrency.format(item.extraAmortization)}")
                    }
                    Text("Saldo devedor: ${formatCurrency.format(item.remainingBalance)}")
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Divider(Modifier.padding(vertical = 8.dp))

        // 📘 Últimas parcelas (sem amortização)
        Text("📘 Últimas parcelas (sem amortização)", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        installmentsWithout.takeLast(3).forEach { item ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
            ) {
                Column(Modifier.padding(10.dp)) {
                    Text("Mês ${item.month}")
                    Text("Amortização: ${formatCurrency.format(item.amortization)}")
                    Text("Juros: ${formatCurrency.format(item.interest)}")
                    Text("Parcela: ${formatCurrency.format(item.installment)}")
                    Text("Saldo devedor: ${formatCurrency.format(item.remainingBalance)}")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 📘 Últimas parcelas (com amortização)
        Text("📘 Últimas parcelas (com amortização)", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        installmentsWith.takeLast(3).forEach { item ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFFBF1))
            ) {
                Column(Modifier.padding(10.dp)) {
                    Text("Mês ${item.month}")
                    Text("Amortização: ${formatCurrency.format(item.amortization)}")
                    Text("Juros: ${formatCurrency.format(item.interest)}")
                    Text("Parcela: ${formatCurrency.format(item.installment)}")
                    if (item.extraAmortization > 0)
                        Text("💰 Extra: ${formatCurrency.format(item.extraAmortization)}")
                    Text("Saldo devedor: ${formatCurrency.format(item.remainingBalance)}")
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "✅ Simulação concluída — total de ${installmentsWith.size} parcelas (com amortização)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SimulationPreviewScreenPreview() {
    SimulationPreviewScreen()
}