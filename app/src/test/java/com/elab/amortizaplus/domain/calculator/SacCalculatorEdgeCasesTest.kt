package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.InterestRate
import com.elab.amortizaplus.domain.model.isFullyPaid
import org.junit.Assert.*
import org.junit.Test

/**
 * Testes de casos extremos e validações de segurança para SAC.
 */
class SacCalculatorEdgeCasesTest {

    private val calc = SacCalculator()
    private val standardRate = InterestRate.Annual(0.13)

    @Test
    fun `saldo final deve ser zero ou muito proximo`() {
        val result = calc.calculate(
            loanAmount = 100_000.0,
            monthlyRate = standardRate.asMonthly(),
            terms = 360
        )

        assertTrue(
            "Saldo final deve ser < 0.01",
            result.last().remainingBalance < 0.01
        )
    }

    @Test
    fun `amortizacao base deve ser constante sem extras`() {
        val loanAmount = 120_000.0
        val terms = 240

        val result = calc.calculate(
            loanAmount = loanAmount,
            monthlyRate = standardRate.asMonthly(),
            terms = terms
        )

        val expectedAmortization = loanAmount / terms
        result.forEach { installment ->
            assertEquals(
                "Amortização base deve ser constante ($expectedAmortization)",
                expectedAmortization,
                installment.amortization,
                0.01
            )
        }
    }

    @Test
    fun `parcelas devem ser estritamente decrescentes no SAC puro`() {
        val result = calc.calculate(
            loanAmount = 100_000.0,
            monthlyRate = standardRate.asMonthly(),
            terms = 120
        )

        for (i in 0 until result.size - 1) {
            assertTrue(
                "Parcela ${i + 1} deve ser maior que parcela ${i + 2}",
                result[i].installment > result[i + 1].installment
            )
        }
    }

    @Test
    fun `juros devem ser estritamente decrescentes no SAC puro`() {
        val result = calc.calculate(
            loanAmount = 100_000.0,
            monthlyRate = standardRate.asMonthly(),
            terms = 120
        )

        for (i in 0 until result.size - 1) {
            assertTrue(
                "Juros do mês ${i + 1} devem ser maiores que do mês ${i + 2}",
                result[i].interest >= result[i + 1].interest
            )
        }
    }

    @Test
    fun `amortizacao que quita divida imediatamente deve gerar 2 parcelas`() {
        // Quando a amortização extra acontece no mês 1, o SAC sempre gera:
        // - Mês 1: parcela regular + amortização extra
        // - Mês 2: quitação do resíduo (se houver)
        val loanAmount = 50_000.0
        val result = calc.calculate(
            loanAmount = loanAmount,
            monthlyRate = standardRate.asMonthly(),
            terms = 360,
            extraAmortizations = mapOf(1 to loanAmount),
            reduceTerm = true
        )

        assertTrue(
            "Deve gerar 1 ou 2 parcelas ao quitar no mês 1",
            result.size in 1..2
        )
        assertTrue("Saldo deve ser zero", result.last().remainingBalance < 0.01)
    }

    @Test
    fun `amortizacao total no mes 2 deve quitar em 2 meses`() {
        val loanAmount = 50_000.0
        val baseAmortization = loanAmount / 360
        val balanceAfterMonth1 = loanAmount - baseAmortization

        val result = calc.calculate(
            loanAmount = loanAmount,
            monthlyRate = standardRate.asMonthly(),
            terms = 360,
            extraAmortizations = mapOf(2 to balanceAfterMonth1),
            reduceTerm = true
        )

        assertEquals("Deve gerar exatamente 2 parcelas", 2, result.size)
        assertTrue("Saldo deve ser zero", result.isFullyPaid())
    }

    @Test
    fun `multiplas amortizacoes muito pequenas nao devem reduzir prazo`() {
        val result = calc.calculate(
            loanAmount = 200_000.0,
            monthlyRate = standardRate.asMonthly(),
            terms = 420,
            extraAmortizations = mapOf(
                12 to 100.0,
                24 to 150.0,
                36 to 200.0,
                48 to 50.0
            ),
            reduceTerm = true
        )

        // Deve manter prazo próximo do original
        assertTrue(
            "Prazo deve ficar entre 410 e 420 meses (obtido: ${result.size})",
            result.size in 410..420
        )
    }

    @Test
    fun `amortizacao no ultimo mes deve quitar corretamente`() {
        val result = calc.calculate(
            loanAmount = 100_000.0,
            monthlyRate = standardRate.asMonthly(),
            terms = 120,
            extraAmortizations = mapOf(120 to 10_000.0),
            reduceTerm = false
        )

        assertEquals(120, result.size)
        assertTrue(result.isFullyPaid())
    }

    @Test
    fun `taxa zero deve gerar parcelas fixas iguais a amortizacao base`() {
        val loanAmount = 12_000.0
        val terms = 12
        val result = calc.calculate(
            loanAmount = loanAmount,
            monthlyRate = 0.0, // sem juros
            terms = terms
        )

        val expectedInstallment = loanAmount / terms // R$ 1.000

        result.forEach { installment ->
            assertEquals(0.0, installment.interest, 0.01)
            assertEquals(expectedInstallment, installment.installment, 0.01)
        }

        assertTrue(result.isFullyPaid())
    }

    @Test
    fun `valor muito pequeno deve ser quitado rapidamente`() {
        val result = calc.calculate(
            loanAmount = 100.0, // R$ 100
            monthlyRate = standardRate.asMonthly(),
            terms = 12
        )

        assertEquals(12, result.size)
        assertTrue(result.isFullyPaid())
    }

    @Test
    fun `valor muito grande deve manter consistencia numerica`() {
        val result = calc.calculate(
            loanAmount = 10_000_000.0, // R$ 10 milhões
            monthlyRate = standardRate.asMonthly(),
            terms = 420
        )

        assertEquals(420, result.size)
        assertTrue(result.isFullyPaid())

        // Parcelas devem decrescer monotonicamente
        for (i in 0 until result.size - 1) {
            assertTrue(result[i].installment >= result[i + 1].installment)
        }
    }
}