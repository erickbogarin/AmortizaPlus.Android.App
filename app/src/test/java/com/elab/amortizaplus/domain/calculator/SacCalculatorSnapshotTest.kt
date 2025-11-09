package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.toSummary
import kotlin.math.pow
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Snapshot simples para garantir que o comportamento validado do SAC continue estável.
 */
class SacCalculatorSnapshotTest {

    private val calculator = SacCalculator()
    private val monthlyRate = (1 + 0.13).pow(1.0 / 12.0) - 1

    @Test
    fun `baseline sac snapshot deve permanecer estavel`() {
        val installments = calculator.calculate(
            loanAmount = 121_000.0,
            monthlyRate = monthlyRate,
            terms = 420
        )

        val summary = installments.toSummary(AmortizationSystem.SAC)

        assertEquals(420, summary.totalMonths)
        assertEquals(381_737.55, summary.totalPaid, 0.01)
        assertEquals(260_737.57, summary.totalInterest, 0.01)
        assertEquals(121_000.0, summary.totalAmortized, 2.0)
        assertEquals(0.0, installments.last().remainingBalance, 0.01)
    }
}
