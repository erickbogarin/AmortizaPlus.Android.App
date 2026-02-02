package com.elab.amortizaplus.data.repository

import com.elab.amortizaplus.data.local.SimulationHistoryDataStore
import com.elab.amortizaplus.domain.model.SavedSimulation
import com.elab.amortizaplus.domain.repository.SimulationHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SimulationHistoryRepositoryImpl(
    private val dataStore: SimulationHistoryDataStore
) : SimulationHistoryRepository {

    override suspend fun saveSimulation(saved: SavedSimulation): Result<Unit> = runCatching {
        val current = dataStore.getSimulations().toMutableList()
        val index = current.indexOfFirst { it.id == saved.id }
        if (index >= 0) {
            current[index] = saved
        } else {
            current.add(saved)
        }
        dataStore.saveSimulations(current.sortedByDescending { it.lastModified })
    }

    override suspend fun getSimulation(id: String): Result<SavedSimulation?> = runCatching {
        dataStore.getSimulations().firstOrNull { it.id == id }
    }

    override suspend fun getAllSimulations(): Result<List<SavedSimulation>> = runCatching {
        dataStore.getSimulations().sortedByDescending { it.lastModified }
    }

    override suspend fun deleteSimulation(id: String): Result<Unit> = runCatching {
        val updated = dataStore.getSimulations().filterNot { it.id == id }
        dataStore.saveSimulations(updated)
    }

    override suspend fun updateSimulation(saved: SavedSimulation): Result<Unit> = runCatching {
        val current = dataStore.getSimulations().toMutableList()
        val index = current.indexOfFirst { it.id == saved.id }
        if (index < 0) {
            throw IllegalArgumentException("Simulation not found: ${saved.id}")
        }
        current[index] = saved
        dataStore.saveSimulations(current.sortedByDescending { it.lastModified })
    }

    override suspend fun searchSimulations(query: String): Result<List<SavedSimulation>> = runCatching {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isBlank()) {
            return@runCatching dataStore.getSimulations().sortedByDescending { it.lastModified }
        }

        dataStore.getSimulations()
            .filter { saved ->
                saved.name.lowercase().contains(normalizedQuery) ||
                    saved.tags.any { tag -> tag.lowercase().contains(normalizedQuery) }
            }
            .sortedByDescending { it.lastModified }
    }

    override fun observeSimulations(): Flow<List<SavedSimulation>> {
        return dataStore.observeSimulations().map { items ->
            items.sortedByDescending { it.lastModified }
        }
    }
}
