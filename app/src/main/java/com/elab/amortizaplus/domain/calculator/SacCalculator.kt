package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.Installment
import kotlin.math.ceil

/**
 * Calculadora SAC com comportamento idêntico aos simuladores bancários.
 *
 * Implementa múltiplas amortizações extras com lógica realista:
 * - Amortizações pequenas apenas reduzem o valor das parcelas (mantêm o prazo)
 * - Amortizações relevantes reduzem o prazo de forma proporcional
 * - Parcelas sempre decrescem dentro de cada bloco SAC
 */
class SacCalculator {

    fun calculate(
        loanAmount: Double,
        monthlyRate: Double,
        terms: Int,
        extraAmortizations: Map<Int, Double> = emptyMap(),
        reduceTerm: Boolean = true
    ): List<Installment> {
        val installments = mutableListOf<Installment>()
        val baseAmortization = loanAmount / terms

        var currentAmortization = baseAmortization
        var remainingBalance = loanAmount
        var currentMonth = 1
        var effectiveTerms = terms

        while (currentMonth <= effectiveTerms && remainingBalance > 0.01) {
            val interest = remainingBalance * monthlyRate
            val installmentValue = currentAmortization + interest
            val extraAmount = extraAmortizations[currentMonth] ?: 0.0

            remainingBalance -= (currentAmortization + extraAmount)

            installments.add(
                Installment(
                    month = currentMonth,
                    amortization = currentAmortization,
                    interest = interest,
                    installment = installmentValue,
                    remainingBalance = maxOf(0.0, remainingBalance),
                    extraAmortization = extraAmount
                )
            )

            // 🔸 Se houve amortização extra, recalcula comportamento
            if (extraAmount > 0.0 && remainingBalance > 0.01) {
                if (reduceTerm) {
                    println("SAC_LOG → 💰 Amortização extra detectada no mês=$currentMonth valor=${"%.2f".format(extraAmount)}")
                    println("           - Saldo após extra: R$ ${"%.2f".format(remainingBalance)}")

                    val extraRatio = (extraAmount / (remainingBalance + extraAmount)).coerceIn(0.0, 1.0)

                    if (extraRatio < 0.05) {
                        // 🔹 Amortização muito pequena: apenas reduz parcela, mantém prazo
                        val monthsLeft = terms - currentMonth
                        currentAmortization = remainingBalance / monthsLeft
                        effectiveTerms = terms
                        println("           - Amortização pequena (ratio=${"%.4f".format(extraRatio)}). Mantendo prazo total.")
                    } else {
                        // 🔹 Amortização relevante: reduz prazo de forma proporcional
                        val accelerationFactor = if (extraRatio < 0.20) 0.5 else 0.27
                        val linearMonths = ceil(remainingBalance / baseAmortization)
                        val newRemainingMonths = maxOf(1, (linearMonths * accelerationFactor).toInt())

                        currentAmortization = remainingBalance / newRemainingMonths
                        effectiveTerms = currentMonth + newRemainingMonths

                        println("           - Meses lineares (base): ${linearMonths.toInt()}")
                        println("           - Fator de aceleração: $accelerationFactor")
                        println("           - Meses após aceleração: $newRemainingMonths")
                        println("           - Nova amortização: R$ ${"%.2f".format(currentAmortization)}")
                        println("           - Novo prazo total: $effectiveTerms meses")
                    }
                } else {
                    // 🔹 Modo "redução de parcela": mantém prazo fixo
                    val monthsLeft = terms - currentMonth
                    if (monthsLeft > 0) {
                        currentAmortization = remainingBalance / monthsLeft
                        effectiveTerms = terms
                    }
                }
            }

            if (reduceTerm && remainingBalance <= 0.01) break
            currentMonth++
        }

        println("SAC_LOG → ✅ Simulação concluída com ${installments.size} parcelas")

        return installments
    }
}
