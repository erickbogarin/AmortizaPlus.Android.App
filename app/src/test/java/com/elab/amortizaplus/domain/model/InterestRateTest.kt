package com.elab.amortizaplus.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.pow

/**
 * Testes para garantir conversão correta entre taxas anuais e mensais.
 */
class InterestRateTest {

    @Test
    fun `taxa anual de 13% deve converter para mensal corretamente`() {
        val annual = InterestRate.Annual(0.13)
        val monthly = annual.asMonthly()

        // Taxa mensal equivalente: (1.13)^(1/12) - 1 ≈ 0.01023
        assertEquals(0.01023, monthly, 0.00001)
    }

    @Test
    fun `taxa mensal deve converter para anual corretamente`() {
        val monthly = InterestRate.Monthly(0.01023)
        val annual = monthly.asAnnual()

        // Taxa anual equivalente: (1.01023)^12 - 1 ≈ 0.13
        assertEquals(0.13, annual, 0.001)
    }

    @Test
    fun `conversao de taxa anual para anual deve retornar mesma taxa`() {
        val annual = InterestRate.Annual(0.13)
        assertEquals(0.13, annual.asAnnual(), 0.0)
    }

    @Test
    fun `conversao de taxa mensal para mensal deve retornar mesma taxa`() {
        val monthly = InterestRate.Monthly(0.01)
        assertEquals(0.01, monthly.asMonthly(), 0.0)
    }

    @Test
    fun `roundtrip anual-mensal-anual deve preservar valor original`() {
        val original = 0.15
        val annual = InterestRate.Annual(original)
        val monthlyValue = annual.asMonthly()
        val monthly = InterestRate.Monthly(monthlyValue)
        val backToAnnual = monthly.asAnnual()

        assertEquals(original, backToAnnual, 0.0001)
    }

    @Test
    fun `taxa zero deve permanecer zero em ambas conversoes`() {
        val annualZero = InterestRate.Annual(0.0)
        assertEquals(0.0, annualZero.asMonthly(), 0.0)

        val monthlyZero = InterestRate.Monthly(0.0)
        assertEquals(0.0, monthlyZero.asAnnual(), 0.0)
    }
}