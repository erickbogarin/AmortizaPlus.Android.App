package com.elab.amortizaplus.domain.usecase

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.ExtraAmortization
import com.elab.amortizaplus.domain.model.ExtraAmortizationStrategy
import com.elab.amortizaplus.domain.model.Simulation
import org.junit.Assert.*
import org.junit.Test

/**
 * Testes para o caso de uso principal de cálculo de financiamento.
 */
class CalculateFinancingUseCaseTest {

    private val useCase = CalculateFinancingUseCase()

    @Test
    fun `deve gerar resultado completo com cenarios comparativos`() {
        val simulation = Simulation(
            loanAmount = 100_000.0,
            interestRate = 0.12, // 12% a.a.
            terms = 240,
            startDate = "2025-01-01",
            amortizationSystem = AmortizationSystem.SAC,
            extraAmortizations = listOf(
                ExtraAmortization(12, 10_000.0, ExtraAmortizationStrategy.REDUCE_TERM)
            ),
            name = "Meu Financiamento"
        )

        val result = useCase(simulation)

        // Validações estruturais
        assertNotNull(result.paymentsWithoutExtra)
        assertNotNull(result.paymentsWithExtra)
        assertNotNull(result.summaryWithoutExtra)
        assertNotNull(result.summaryWithExtra)
        assertEquals(simulation, result.simulation)

        // Validações de consistência
        assertTrue(
            "Prazo sem extras deve ser maior ou igual",
            result.paymentsWithoutExtra.size >= result.paymentsWithExtra.size
        )

        assertTrue(
            "Juros sem extras devem ser maiores",
            result.summaryWithoutExtra.totalInterest >= result.summaryWithExtra.totalInterest
        )

        // Economia deve estar calculada
        assertTrue(result.summaryWithExtra.interestSavings > 0)
        assertTrue(result.summaryWithExtra.reducedMonths >= 0)
    }

    @Test
    fun `sem amortizacoes extras summaries devem ser identicos`() {
        val simulation = Simulation(
            loanAmount = 50_000.0,
            interestRate = 0.10,
            terms = 120,
            startDate = "2025-01-01",
            amortizationSystem = AmortizationSystem.PRICE,
            extraAmortizations = emptyList(),
            name = "Sem Extras"
        )

        val result = useCase(simulation)

        // Sem extras, os totais devem ser iguais
        assertEquals(
            result.summaryWithoutExtra.totalPaid,
            result.summaryWithExtra.totalPaid,
            100.0 // tolerância de R$ 100 por arredondamentos
        )

        assertEquals(
            result.summaryWithoutExtra.totalMonths,
            result.summaryWithExtra.totalMonths
        )
    }

    @Test
    fun `multiplas amortizacoes devem ser processadas corretamente`() {
        val simulation = Simulation(
            loanAmount = 121_000.0,
            interestRate = 0.13,
            terms = 420,
            startDate = "2025-01-01",
            amortizationSystem = AmortizationSystem.SAC,
            extraAmortizations = listOf(
                ExtraAmortization(8, 76_000.0, ExtraAmortizationStrategy.REDUCE_TERM),
                ExtraAmortization(16, 10_000.0, ExtraAmortizationStrategy.REDUCE_TERM)
            ),
            name = "Múltiplas Amortizações"
        )

        val result = useCase(simulation)

        // Deve ter aplicado ambas amortizações
        val month8 = result.paymentsWithExtra.first { it.month == 8 }
        val month16 = result.paymentsWithExtra.first { it.month == 16 }

        assertEquals(76_000.0, month8.extraAmortization, 0.01)
        assertEquals(10_000.0, month16.extraAmortization, 0.01)

        // Prazo deve estar significativamente reduzido
        val reduction = result.summaryWithoutExtra.totalMonths - result.summaryWithExtra.totalMonths
        assertTrue("Redução deve ser >= 300 meses (foi $reduction)", reduction >= 300)
    }

    @Test
    fun `sistema PRICE deve manter parcelas fixas na baseline`() {
        val simulation = Simulation(
            loanAmount = 80_000.0,
            interestRate = 0.11,
            terms = 180,
            startDate = "2025-01-01",
            amortizationSystem = AmortizationSystem.PRICE,
            extraAmortizations = emptyList(),
            name = "PRICE Baseline"
        )

        val result = useCase(simulation)

        val payments = result.paymentsWithoutExtra
        val firstInstallment = payments.first().installment
        val lastInstallment = payments.last().installment

        assertEquals(
            "PRICE deve ter parcelas fixas",
            firstInstallment,
            lastInstallment,
            0.01
        )
    }

    @Test
    fun `sistema SAC deve ter parcelas decrescentes na baseline`() {
        val simulation = Simulation(
            loanAmount = 80_000.0,
            interestRate = 0.11,
            terms = 180,
            startDate = "2025-01-01",
            amortizationSystem = AmortizationSystem.SAC,
            extraAmortizations = emptyList(),
            name = "SAC Baseline"
        )

        val result = useCase(simulation)

        val payments = result.paymentsWithoutExtra
        val firstInstallment = payments.first().installment
        val lastInstallment = payments.last().installment

        assertTrue(
            "SAC deve ter parcelas decrescentes",
            firstInstallment > lastInstallment
        )
    }

    @Test
    fun `resultado deve preservar dados da simulacao original`() {
        val simulation = Simulation(
            loanAmount = 150_000.0,
            interestRate = 0.135,
            terms = 300,
            startDate = "2025-06-01",
            amortizationSystem = AmortizationSystem.SAC,
            extraAmortizations = emptyList(),
            name = "Teste Preservação"
        )

        val result = useCase(simulation)

        assertEquals(simulation.loanAmount, result.simulation.loanAmount, 0.0)
        assertEquals(simulation.interestRate, result.simulation.interestRate, 0.0)
        assertEquals(simulation.terms, result.simulation.terms)
        assertEquals(simulation.startDate, result.simulation.startDate)
        assertEquals(simulation.amortizationSystem, result.simulation.amortizationSystem)
        assertEquals(simulation.name, result.simulation.name)
    }
}