package com.elab.amortizaplus.domain.model

data class SavedSimulation(
    val id: String,
    val name: String,
    val createdAt: Long,
    val lastModified: Long,
    val simulation: Simulation,
    val result: SavedSimulationResult,
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList()
)

data class SavedSimulationResult(
    val summaryWithoutExtra: SimulationSummary,
    val summaryWithExtra: SimulationSummary
)
