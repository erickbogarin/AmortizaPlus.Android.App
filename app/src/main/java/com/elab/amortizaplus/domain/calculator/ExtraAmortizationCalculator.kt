package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.Installment
import com.elab.amortizaplus.domain.model.SimulationSummary

class ExtraAmortizationCalculator {

    fun calculateSummary(installments: List<Installment>): SimulationSummary {
        val totalPaid = installments.sumOf { it.installment + it.extraAmortization }
        val totalInterest = installments.sumOf { it.interest }
        val totalAmortized = installments.sumOf { it.amortization }
        val totalMonths = installments.size

        return SimulationSummary(
            system = null, // opcional — pode ser setado no UseCase se necessário
            totalPaid = totalPaid,
            totalInterest = totalInterest,
            totalAmortized = totalAmortized,
            totalMonths = totalMonths
        )
    }
}