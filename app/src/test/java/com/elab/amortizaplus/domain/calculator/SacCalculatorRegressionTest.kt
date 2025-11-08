package com.elab.amortizaplus.domain.calculator

import org.junit.Test
import org.junit.Assert.assertEquals
import kotlin.math.pow

class SacCalculatorRegressionTest {

    @Test
    fun `SAC sem amortizacao deve gerar 420 parcelas e quitar corretamente`() {
        val calc = SacCalculator()
        val rate = (1 + 0.13).pow(1.0 / 12.0) - 1

        val result = calc.calculate(
            loanAmount = 121_000.0,
            monthlyRate = rate,
            terms = 420
        )

        assertEquals(420, result.size)
        assertEquals(0.0, result.last().remainingBalance, 0.01)
    }

    @Test
    fun `SAC com amortizacao de 76k no mes 8 deve continuar quitando em 48 meses`() {
        val calc = SacCalculator()
        val rate = (1 + 0.13).pow(1.0 / 12.0) - 1

        val result = calc.calculate(
            loanAmount = 121_000.0,
            monthlyRate = rate,
            terms = 420,
            extraAmortizations = mapOf(8 to 76_000.0),
            reduceTerm = true
        )

        assertEquals(48, result.size)
        assertEquals(0.0, result.last().remainingBalance, 0.01)
    }

    @Test
    fun `SAC com amortizacao mas sem reduzir prazo deve manter 420 meses`() {
        val calc = SacCalculator()
        val rate = (1 + 0.13).pow(1.0 / 12.0) - 1

        val result = calc.calculate(
            loanAmount = 121_000.0,
            monthlyRate = rate,
            terms = 420,
            extraAmortizations = mapOf(8 to 76_000.0),
            reduceTerm = false
        )

        assertEquals(420, result.size)
    }
}
