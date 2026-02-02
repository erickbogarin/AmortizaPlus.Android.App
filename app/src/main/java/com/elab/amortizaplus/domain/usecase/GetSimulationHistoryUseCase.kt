package com.elab.amortizaplus.domain.usecase

import com.elab.amortizaplus.domain.model.SavedSimulation
import com.elab.amortizaplus.domain.repository.SimulationHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetSimulationHistoryUseCase(
    private val repository: SimulationHistoryRepository
) {
    operator fun invoke(): Flow<List<SavedSimulation>> = repository.observeSimulations()
}
