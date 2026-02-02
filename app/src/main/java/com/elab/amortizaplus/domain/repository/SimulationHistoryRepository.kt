package com.elab.amortizaplus.domain.repository

import com.elab.amortizaplus.domain.model.SavedSimulation
import kotlinx.coroutines.flow.Flow

interface SimulationHistoryRepository {
    suspend fun saveSimulation(saved: SavedSimulation): Result<Unit>
    suspend fun getSimulation(id: String): Result<SavedSimulation?>
    suspend fun getAllSimulations(): Result<List<SavedSimulation>>
    suspend fun deleteSimulation(id: String): Result<Unit>
    suspend fun updateSimulation(saved: SavedSimulation): Result<Unit>
    suspend fun searchSimulations(query: String): Result<List<SavedSimulation>>
    fun observeSimulations(): Flow<List<SavedSimulation>>
}
