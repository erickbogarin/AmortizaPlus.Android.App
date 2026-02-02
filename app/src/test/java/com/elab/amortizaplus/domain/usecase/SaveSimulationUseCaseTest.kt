package com.elab.amortizaplus.domain.usecase

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.ExtraAmortization
import com.elab.amortizaplus.domain.model.ExtraAmortizationStrategy
import com.elab.amortizaplus.domain.model.Installment
import com.elab.amortizaplus.domain.model.InterestRateType
import com.elab.amortizaplus.domain.model.Simulation
import com.elab.amortizaplus.domain.model.SimulationResult
import com.elab.amortizaplus.domain.model.SimulationSummary
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveSimulationUseCaseTest {

    private val repository = FakeSimulationHistoryRepository()
    private val useCase = SaveSimulationUseCase(repository)

    @Test
    fun `should save simulation with generated metadata`() = runBlocking {
        val simulation = sampleSimulation()
        val result = sampleResult(simulation)

        val savedResult = useCase(simulation = simulation, result = result)

        assertTrue(savedResult.isSuccess)
        val saved = requireNotNull(savedResult.getOrNull())
        assertTrue(saved.id.isNotBlank())
        assertTrue(saved.name.startsWith("Simulacao"))
        assertEquals(result.summaryWithoutExtra, saved.result.summaryWithoutExtra)
        assertEquals(result.summaryWithExtra, saved.result.summaryWithExtra)

        val allSaved = repository.getAllSimulations().getOrNull()
        assertEquals(1, allSaved?.size)
    }

    @Test
    fun `should respect explicit name and tags`() = runBlocking {
        val simulation = sampleSimulation()
        val result = sampleResult(simulation)

        val savedResult = useCase(
            simulation = simulation,
            result = result,
            name = "Apartamento Centro",
            tags = listOf("casa", "urgente")
        )

        val saved = requireNotNull(savedResult.getOrNull())
        assertEquals("Apartamento Centro", saved.name)
        assertEquals(listOf("casa", "urgente"), saved.tags)
    }

    private fun sampleSimulation(): Simulation {
        return Simulation(
            loanAmount = 250_000.0,
            interestRate = 0.1,
            rateType = InterestRateType.ANNUAL,
            terms = 360,
            startDate = "2026-02",
            amortizationSystem = AmortizationSystem.SAC,
            extraAmortizations = listOf(
                ExtraAmortization(
                    month = 12,
                    amount = 50_000.0,
                    strategy = ExtraAmortizationStrategy.REDUCE_TERM
                )
            ),
            name = "Cenario SAC"
        )
    }

    private fun sampleResult(simulation: Simulation): SimulationResult {
        val installment = Installment(
            month = 1,
            amortization = 500.0,
            interest = 2000.0,
            installment = 2500.0,
            remainingBalance = 249_500.0
        )

        return SimulationResult(
            simulation = simulation,
            paymentsWithoutExtra = listOf(installment),
            paymentsWithExtra = listOf(installment.copy(extraAmortization = 50_000.0)),
            summaryWithoutExtra = SimulationSummary(
                system = simulation.amortizationSystem,
                totalPaid = 450_000.0,
                totalInterest = 200_000.0,
                totalAmortized = 250_000.0,
                totalMonths = 360
            ),
            summaryWithExtra = SimulationSummary(
                system = simulation.amortizationSystem,
                totalPaid = 380_000.0,
                totalInterest = 130_000.0,
                totalAmortized = 250_000.0,
                totalMonths = 300,
                reducedMonths = 60,
                interestSavings = 70_000.0
            )
        )
    }
}
