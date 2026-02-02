package com.elab.amortizaplus.domain.usecase

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.SavedSimulation
import com.elab.amortizaplus.domain.model.SavedSimulationResult
import com.elab.amortizaplus.domain.model.Simulation
import com.elab.amortizaplus.domain.model.SimulationResult
import com.elab.amortizaplus.domain.repository.SimulationHistoryRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SaveSimulationUseCase(
    private val repository: SimulationHistoryRepository
) {
    suspend operator fun invoke(
        simulation: Simulation,
        result: SimulationResult,
        name: String? = null,
        tags: List<String> = emptyList()
    ): Result<SavedSimulation> {
        val now = System.currentTimeMillis()
        val resolvedName = name
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: generateDefaultName(simulation.amortizationSystem, now)

        val saved = SavedSimulation(
            id = UUID.randomUUID().toString(),
            name = resolvedName,
            createdAt = now,
            lastModified = now,
            simulation = simulation,
            result = SavedSimulationResult(
                summaryWithoutExtra = result.summaryWithoutExtra,
                summaryWithExtra = result.summaryWithExtra
            ),
            tags = tags
        )

        return repository.saveSimulation(saved).map { saved }
    }

    private fun generateDefaultName(system: AmortizationSystem, timestamp: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return "Simulacao ${system.name} - ${formatter.format(Date(timestamp))}"
    }
}
