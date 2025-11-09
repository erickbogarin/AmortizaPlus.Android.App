package com.elab.amortizaplus.domain.calculator

import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.pow

class SacCalculatorStabilityTest {

    @Test
    fun `SAC deve permanecer estavel em prazos muito longos e taxa muito baixa`() {
        val calc = SacCalculator()
        val rate = (1 + 0.0001).pow(1.0 / 12.0) - 1 // taxa praticamente zero
        val loanAmount = 500_000.0
        val terms = 600 // 50 anos!

        val result = calc.calculate(
            loanAmount = loanAmount,
            monthlyRate = rate,
            terms = terms
        )

        // Deve gerar o número correto de parcelas
        assertEquals(terms, result.size)

        // Nenhum valor deve ser inválido
        result.forEachIndexed { i, inst ->
            assertTrue("Amortização inválida no mês ${i + 1}", inst.amortization >= 0.0)
            assertTrue("Juros inválidos no mês ${i + 1}", inst.interest >= 0.0)
            assertTrue("Saldo negativo no mês ${i + 1}", inst.remainingBalance >= 0.0)
        }

        // Saldo final deve ser muito pequeno
        assertTrue(result.last().remainingBalance < 0.01)

        // Soma da amortização deve ser praticamente o empréstimo
        val totalAmortized = result.sumOf { it.amortization } + result.last().extraAmortization
        assertEquals(loanAmount, totalAmortized, 5.0)
    }
}
