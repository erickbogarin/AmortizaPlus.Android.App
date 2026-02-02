package com.elab.amortizaplus.domain.usecase

import com.elab.amortizaplus.domain.repository.SimulationHistoryRepository

class DeleteSimulationUseCase(
    private val repository: SimulationHistoryRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        if (id.isBlank()) {
            return Result.failure(IllegalArgumentException("Simulation id cannot be blank"))
        }
        return repository.deleteSimulation(id)
    }
}
