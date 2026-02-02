package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.InterestRate
import com.elab.amortizaplus.domain.model.toSummary
import kotlin.math.pow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancingCalculatorTest {

    private val calculator = FinancingCalculator(
        sacCalculator = SacCalculator(),
        priceCalculator = PriceCalculator()
    )

    @Test
    fun `compare deve retornar economia e reducao de prazo`() {
        val extras = mapOf(
            8 to ExtraAmortizationInput(70_000.0, reduceTerm = true),
            24 to ExtraAmortizationInput(10_000.0, reduceTerm = true)
        )

        val (without, with) = calculator.compare(
            loanAmount = 150_000.0,
            rate = InterestRate.Annual(0.13),
            terms = 420,
            system = AmortizationSystem.SAC,
            extraAmortizations = extras
        )

        assertTrue(with.interestSavings > 0)
        assertTrue(with.reducedMonths > 0)
        assertTrue(with.totalPaid < without.totalPaid)
    }

    @Test
    fun `calculate deve aplicar estrategias distintas por amortizacao`() {
        val extras = mapOf(
            12 to ExtraAmortizationInput(40_000.0, reduceTerm = true),
            24 to ExtraAmortizationInput(20_000.0, reduceTerm = false)
        )

        val installments = calculator.calculate(
            loanAmount = 180_000.0,
            rate = InterestRate.Annual(0.11),
            terms = 240,
            system = AmortizationSystem.PRICE,
            extraAmortizations = extras
        )

        val month12 = installments.first { it.month == 12 }
        val month13 = installments.first { it.month == 13 }
        val month24 = installments.first { it.month == 24 }
        val month25 = installments.first { it.month == 25 }

        assertEquals(40_000.0, month12.extraAmortization, 0.01)
        assertEquals(20_000.0, month24.extraAmortization, 0.01)
        assertTrue("Parcela após reduzir pagamento deve cair", month25.installment < month24.installment)
        assertTrue("Redução de prazo deve ocorrer após primeiro aporte", installments.size < 240)
    }

    @Test
    fun `taxas mensais e anuais equivalentes devem produzir resultados similares`() {
        val monthlyRate = InterestRate.Monthly(0.01)
        val annualEquivalent = InterestRate.Annual((1 + 0.01).pow(12.0) - 1)

        val monthlyResult = calculator.calculate(
            loanAmount = 90_000.0,
            rate = monthlyRate,
            terms = 180,
            system = AmortizationSystem.PRICE
        )

        val annualResult = calculator.calculate(
            loanAmount = 90_000.0,
            rate = annualEquivalent,
            terms = 180,
            system = AmortizationSystem.PRICE
        )

        assertEquals(monthlyResult.first().installment, annualResult.first().installment, 0.01)
        assertEquals(monthlyResult.last().installment, annualResult.last().installment, 0.01)
        assertEquals(monthlyResult.size, annualResult.size)
    }

    @Test
    fun `resumo deve preservar total amortizado nos dois sistemas`() {
        val sacSummary = calculator.calculate(
            loanAmount = 120_000.0,
            rate = InterestRate.Annual(0.1),
            terms = 240,
            system = AmortizationSystem.SAC
        ).toSummary(AmortizationSystem.SAC)

        val priceSummary = calculator.calculate(
            loanAmount = 120_000.0,
            rate = InterestRate.Annual(0.1),
            terms = 240,
            system = AmortizationSystem.PRICE
        ).toSummary(AmortizationSystem.PRICE)

        assertEquals(120_000.0, sacSummary.totalAmortized, 0.01)
        assertEquals(120_000.0, priceSummary.totalAmortized, 0.01)
    }
}
