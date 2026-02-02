package com.elab.amortizaplus.domain.usecase

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.SavedSimulation
import com.elab.amortizaplus.domain.model.SavedSimulationResult
import com.elab.amortizaplus.domain.model.Simulation
import com.elab.amortizaplus.domain.model.SimulationSummary
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class GetSimulationHistoryUseCaseTest {

    private val repository = FakeSimulationHistoryRepository()
    private val getHistoryUseCase = GetSimulationHistoryUseCase(repository)
    private val getByIdUseCase = GetSimulationByIdUseCase(repository)

    @Test
    fun `history use case should emit saved simulations ordered by last update`() = runBlocking {
        val first = sampleSaved(id = "1", name = "Primeira", lastModified = 10L)
        val second = sampleSaved(id = "2", name = "Segunda", lastModified = 20L)

        repository.saveSimulation(first)
        repository.saveSimulation(second)

        val emitted = getHistoryUseCase().first()

        assertEquals(2, emitted.size)
        assertEquals("2", emitted[0].id)
        assertEquals("1", emitted[1].id)
    }

    @Test
    fun `get by id use case should return saved simulation`() = runBlocking {
        val saved = sampleSaved(id = "abc", name = "Meu Cenário", lastModified = 100L)
        repository.saveSimulation(saved)

        val result = getByIdUseCase("abc")
        val loaded = result.getOrNull()

        assertNotNull(loaded)
        assertEquals("Meu Cenário", loaded?.name)
    }

    private fun sampleSaved(id: String, name: String, lastModified: Long): SavedSimulation {
        return SavedSimulation(
            id = id,
            name = name,
            createdAt = 1L,
            lastModified = lastModified,
            simulation = Simulation(
                loanAmount = 100_000.0,
                interestRate = 0.1,
                terms = 240,
                startDate = "2026-02",
                amortizationSystem = AmortizationSystem.SAC,
                extraAmortizations = emptyList(),
                name = name
            ),
            result = SavedSimulationResult(
                summaryWithoutExtra = SimulationSummary(
                    system = AmortizationSystem.SAC,
                    totalPaid = 180_000.0,
                    totalInterest = 80_000.0,
                    totalAmortized = 100_000.0,
                    totalMonths = 240
                ),
                summaryWithExtra = SimulationSummary(
                    system = AmortizationSystem.SAC,
                    totalPaid = 170_000.0,
                    totalInterest = 70_000.0,
                    totalAmortized = 100_000.0,
                    totalMonths = 220,
                    reducedMonths = 20,
                    interestSavings = 10_000.0
                )
            )
        )
    }
}
