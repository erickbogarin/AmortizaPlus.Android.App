package com.elab.amortizaplus.domain.usecase

import com.elab.amortizaplus.domain.model.SavedSimulation
import com.elab.amortizaplus.domain.repository.SimulationHistoryRepository

class GetSimulationByIdUseCase(
    private val repository: SimulationHistoryRepository
) {
    suspend operator fun invoke(id: String): Result<SavedSimulation?> {
        return repository.getSimulation(id)
    }
}
