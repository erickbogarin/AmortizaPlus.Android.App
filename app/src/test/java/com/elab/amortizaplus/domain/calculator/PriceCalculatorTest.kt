package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.isFullyPaid
import kotlin.math.pow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PriceCalculatorTest {

    private val calculator = PriceCalculator()
    private val monthlyRate = (1 + 0.11).pow(1.0 / 12.0) - 1

    @Test
    fun `baseline price deve manter parcelas fixas`() {
        val installments = calculator.calculate(
            loanAmount = 200_000.0,
            monthlyRate = monthlyRate,
            terms = 240
        )

        assertEquals(240, installments.size)
        assertEquals(installments.first().installment, installments.last().installment, 0.1)
        assertTrue(installments.isFullyPaid())
    }

    @Test
    fun `amortizacao extra reduz prazo quando configurada para reduce term`() {
        val installments = calculator.calculate(
            loanAmount = 150_000.0,
            monthlyRate = monthlyRate,
            terms = 240,
            extraAmortizations = mapOf(12 to ExtraAmortizationInput(50_000.0, reduceTerm = true))
        )

        assertTrue("Prazo deve reduzir pelo menos 60 meses", installments.size <= 180)
        assertTrue(installments.isFullyPaid())
    }

    @Test
    fun `amortizacao extra com reduce payment deve recalcular parcela`() {
        val installments = calculator.calculate(
            loanAmount = 150_000.0,
            monthlyRate = monthlyRate,
            terms = 240,
            extraAmortizations = mapOf(12 to ExtraAmortizationInput(50_000.0, reduceTerm = false))
        )

        assertEquals(240, installments.size)

        val installmentBefore = installments.first { it.month == 12 }.installment
        val installmentAfter = installments.first { it.month == 13 }.installment
        assertTrue("Parcela após recálculo deve ser menor", installmentAfter < installmentBefore)
    }

    @Test
    fun `price deve lidar com taxa zero`() {
        val installments = calculator.calculate(
            loanAmount = 12_000.0,
            monthlyRate = 0.0,
            terms = 12
        )

        assertEquals(12, installments.size)
        installments.forEach { installment ->
            assertEquals(1_000.0, installment.installment, 0.01)
            assertEquals(0.0, installment.interest, 0.0)
        }
        assertTrue(installments.isFullyPaid())
    }

    @Test
    fun `amortizacao grande pode quitar financiamento rapidamente`() {
        val installments = calculator.calculate(
            loanAmount = 80_000.0,
            monthlyRate = monthlyRate,
            terms = 240,
            extraAmortizations = mapOf(6 to ExtraAmortizationInput(60_000.0, reduceTerm = true))
        )

        assertTrue("Deve quitar em menos de 100 parcelas", installments.size < 100)
        assertTrue(installments.isFullyPaid())
    }

    @Test
    fun `price deve lidar com taxa muito baixa`() {
        val rate = (1 + 0.0001).pow(1.0 / 12.0) - 1
        val installments = calculator.calculate(loanAmount = 100_000.0, monthlyRate = rate, terms = 360)
        assertTrue(installments.isFullyPaid())
        assertEquals(360, installments.size)
    }

    @Test
    fun `price deve lidar com taxa muito alta`() {
        val rate = (1 + 0.5).pow(1.0 / 12.0) - 1 // 50% a.a.
        val installments = calculator.calculate(loanAmount = 100_000.0, monthlyRate = rate, terms = 60)
        assertTrue(installments.isFullyPaid())
        assertEquals(60, installments.size)
    }

    @Test
    fun `amortizacao tardia deve reduzir saldo final sem quebrar loop`() {
        val installments = calculator.calculate(
            loanAmount = 200_000.0,
            monthlyRate = monthlyRate,
            terms = 240,
            extraAmortizations = mapOf(200 to ExtraAmortizationInput(20_000.0, reduceTerm = true))
        )
        assertTrue(installments.isFullyPaid())
        assertTrue(installments.size <= 240)
    }

    @Test
    fun `pequenos saldos residuais devem ser quitados corretamente`() {
        val installments = calculator.calculate(
            loanAmount = 100.0,
            monthlyRate = monthlyRate,
            terms = 12
        )
        assertTrue(installments.isFullyPaid())
        assertEquals(12, installments.size)
    }

    @Test
    fun `reduce term seguido de reduce payment deve manter coerencia`() {
        val extras = mapOf(
            6 to ExtraAmortizationInput(30_000.0, reduceTerm = true),
            18 to ExtraAmortizationInput(10_000.0, reduceTerm = false)
        )
        val installments = calculator.calculate(
            loanAmount = 150_000.0,
            monthlyRate = monthlyRate,
            terms = 240,
            extraAmortizations = extras
        )
        assertTrue(installments.isFullyPaid())
        assertTrue(installments.size < 240)
    }
}
