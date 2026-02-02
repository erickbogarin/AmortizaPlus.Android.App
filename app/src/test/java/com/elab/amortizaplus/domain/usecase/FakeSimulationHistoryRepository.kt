package com.elab.amortizaplus.domain.usecase

import com.elab.amortizaplus.domain.model.SavedSimulation
import com.elab.amortizaplus.domain.repository.SimulationHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSimulationHistoryRepository : SimulationHistoryRepository {
    private val itemsFlow = MutableStateFlow<List<SavedSimulation>>(emptyList())

    override suspend fun saveSimulation(saved: SavedSimulation): Result<Unit> = runCatching {
        val current = itemsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == saved.id }
        if (index >= 0) {
            current[index] = saved
        } else {
            current.add(saved)
        }
        itemsFlow.value = current.sortedByDescending { it.lastModified }
    }

    override suspend fun getSimulation(id: String): Result<SavedSimulation?> = runCatching {
        itemsFlow.value.firstOrNull { it.id == id }
    }

    override suspend fun getAllSimulations(): Result<List<SavedSimulation>> = runCatching {
        itemsFlow.value.sortedByDescending { it.lastModified }
    }

    override suspend fun deleteSimulation(id: String): Result<Unit> = runCatching {
        itemsFlow.value = itemsFlow.value.filterNot { it.id == id }
    }

    override suspend fun updateSimulation(saved: SavedSimulation): Result<Unit> = runCatching {
        val current = itemsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == saved.id }
        if (index < 0) {
            throw IllegalArgumentException("Simulation not found: ${saved.id}")
        }
        current[index] = saved
        itemsFlow.value = current.sortedByDescending { it.lastModified }
    }

    override suspend fun searchSimulations(query: String): Result<List<SavedSimulation>> = runCatching {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) {
            return@runCatching itemsFlow.value
        }
        itemsFlow.value.filter { saved ->
            saved.name.lowercase().contains(normalized) ||
                saved.tags.any { it.lowercase().contains(normalized) }
        }
    }

    override fun observeSimulations(): Flow<List<SavedSimulation>> = itemsFlow.asStateFlow()
}
