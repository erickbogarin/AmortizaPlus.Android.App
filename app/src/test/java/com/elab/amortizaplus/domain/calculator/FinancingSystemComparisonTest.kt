package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.InterestRate
import com.elab.amortizaplus.domain.model.toSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

class FinancingSystemComparisonTest {

    @Test
    fun `PRICE deve gerar juros totais maiores que SAC no mesmo cenario`() {
        val calc = FinancingCalculator(
            sacCalculator = SacCalculator(),
            priceCalculator = PriceCalculator()
        )
        val rate = InterestRate.Annual(0.11)
        val loanAmount = 200_000.0
        val terms = 240

        val sac = calc.calculate(
            loanAmount = loanAmount,
            rate = rate,
            terms = terms,
            system = AmortizationSystem.SAC
        ).toSummary(AmortizationSystem.SAC)

        val price = calc.calculate(
            loanAmount = loanAmount,
            rate = rate,
            terms = terms,
            system = AmortizationSystem.PRICE
        ).toSummary(AmortizationSystem.PRICE)

        // Ambos devem amortizar o valor total
        assertEquals(loanAmount, sac.totalAmortized, 1.0)
        assertEquals(loanAmount, price.totalAmortized, 1.0)

        // PRICE sempre gera mais juros totais que SAC
        assertTrue(
            "PRICE deve ter juros maiores (SAC=${sac.totalInterest}, PRICE=${price.totalInterest})",
            price.totalInterest > sac.totalInterest
        )

        // PRICE também deve ter total pago maior
        assertTrue(price.totalPaid > sac.totalPaid)
    }
}
