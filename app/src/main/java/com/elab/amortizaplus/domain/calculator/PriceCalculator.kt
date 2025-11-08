package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.Installment
import kotlin.math.max
import kotlin.math.pow

/**
 * Calculadora para o sistema PRICE (Tabela Francesa)
 */
class PriceCalculator {

    fun calculate(
        loanAmount: Double,
        monthlyRate: Double,
        terms: Int,
        extraAmortizations: Map<Int, Double> = emptyMap(),
        reduceTerm: Boolean = true
    ): List<Installment> {
        val installments = mutableListOf<Installment>()

        // Fórmula da prestação fixa (PMT)
        val pmt = loanAmount * monthlyRate / (1 - (1 + monthlyRate).pow(-terms))
        var remaining = loanAmount

        for (month in 1..terms) {
            val interest = remaining * monthlyRate
            val amortization = pmt - interest

            // amortização extra (ex: FGTS, aporte único)
            val extra = extraAmortizations[month] ?: 0.0

            remaining -= amortization + extra
            remaining = max(0.0, remaining)

            installments.add(
                Installment(
                    month = month,
                    amortization = amortization,
                    interest = interest,
                    installment = pmt,
                    remainingBalance = remaining,
                    extraAmortization = extra
                )
            )

            // se for redução de prazo, encerra ao quitar o saldo
            if (reduceTerm && remaining <= 0.0) break
        }

        return installments
    }
}