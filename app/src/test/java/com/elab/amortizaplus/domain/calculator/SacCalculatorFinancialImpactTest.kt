package com.elab.amortizaplus.domain.calculator

import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import kotlin.math.pow

/**
 * Teste de impacto financeiro total:
 * Compara o total pago, juros e parcelas entre
 * - cenário base (sem amortização)
 * - cenário com amortizações extras
 */
class SacCalculatorFinancialImpactTest {

    private val annualRate = 0.13
    private val monthlyRate = (1 + annualRate).pow(1.0 / 12.0) - 1

    private val loanAmount = 121_000.0
    private val terms = 420

    @Test
    fun `amortizacoes devem reduzir total pago e juros significativamente`() {
        val calc = SacCalculator()

        // 🔹 Cenário sem amortização
        val base = calc.calculate(
            loanAmount = loanAmount,
            monthlyRate = monthlyRate,
            terms = terms,
            extraAmortizations = emptyMap()
        )

        val totalBasePaid = base.sumOf { it.installment }
        val totalBaseInterest = base.sumOf { it.interest }

        // 🔹 Cenário com amortizações (realista)
        val withAmort = calc.calculate(
            loanAmount = loanAmount,
            monthlyRate = monthlyRate,
            terms = terms,
            extraAmortizations = mapOf(
                8 to ExtraAmortizationInput(76_000.0, reduceTerm = true),
                16 to ExtraAmortizationInput(10_000.0, reduceTerm = true)
            )
        )

        val totalWithPaid = withAmort.sumOf { it.installment }
        val totalWithInterest = withAmort.sumOf { it.interest }

        // 🔍 Verificações principais
        assertTrue(
            "O total pago com amortizações deve ser menor que o total base",
            totalWithPaid < totalBasePaid
        )

        assertTrue(
            "Os juros totais devem cair de forma significativa (> 50%)",
            totalWithInterest < totalBaseInterest * 0.5
        )

        assertTrue(
            "O prazo deve reduzir pelo menos 300 meses",
            base.size - withAmort.size >= 300
        )

        // Tolerância de arredondamento
        assertEquals(38.0, withAmort.size.toDouble(), 2.0)
    }

    @Test
    fun `pequenas amortizacoes devem reduzir pouco o total de juros`() {
        val calc = SacCalculator()

        val base = calc.calculate(
            loanAmount = loanAmount,
            monthlyRate = monthlyRate,
            terms = terms
        )
        val totalBaseInterest = base.sumOf { it.interest }

        val withSmallAmort = calc.calculate(
            loanAmount = loanAmount,
            monthlyRate = monthlyRate,
            terms = terms,
            extraAmortizations = mapOf(
                12 to ExtraAmortizationInput(1_000.0, reduceTerm = true),
                24 to ExtraAmortizationInput(500.0, reduceTerm = true),
                36 to ExtraAmortizationInput(1_500.0, reduceTerm = true)
            )
        )
        val totalWithInterest = withSmallAmort.sumOf { it.interest }

        // Deve haver economia leve, mas não desproporcional
        assertTrue(
            "Pequenas amortizações devem reduzir os juros em menos de 5%",
            totalBaseInterest - totalWithInterest < totalBaseInterest * 0.05
        )

        assertEquals(
            "Prazo deve continuar o mesmo (420 meses)",
            420,
            withSmallAmort.size
        )
    }
}
