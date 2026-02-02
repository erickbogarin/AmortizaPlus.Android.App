package com.elab.amortizaplus.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.ExtraAmortization
import com.elab.amortizaplus.domain.model.ExtraAmortizationStrategy
import com.elab.amortizaplus.domain.model.InterestRateType
import com.elab.amortizaplus.domain.model.SavedSimulation
import com.elab.amortizaplus.domain.model.SavedSimulationResult
import com.elab.amortizaplus.domain.model.Simulation
import com.elab.amortizaplus.domain.model.SimulationSummary
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class SimulationHistoryDataStore(
    private val context: Context
) {
    private val dataStore: DataStore<Preferences> = androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile(DATASTORE_NAME) }
    )

    fun observeSimulations(): Flow<List<SavedSimulation>> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val payload = preferences[SIMULATIONS_KEY]
                decodeList(payload)
            }
    }

    suspend fun getSimulations(): List<SavedSimulation> {
        val preferences = dataStore.data.first()
        return decodeList(preferences[SIMULATIONS_KEY])
    }

    suspend fun saveSimulations(simulations: List<SavedSimulation>) {
        dataStore.edit { preferences ->
            preferences[SIMULATIONS_KEY] = encodeList(simulations)
        }
    }

    private fun decodeList(payload: String?): List<SavedSimulation> {
        if (payload.isNullOrBlank()) return emptyList()

        return runCatching {
            val jsonArray = JSONArray(payload)
            buildList {
                for (index in 0 until jsonArray.length()) {
                    val item = jsonArray.optJSONObject(index) ?: continue
                    val decoded = decodeSavedSimulation(item) ?: continue
                    add(decoded)
                }
            }.sortedByDescending { it.lastModified }
        }.getOrElse {
            emptyList()
        }
    }

    private fun encodeList(simulations: List<SavedSimulation>): String {
        val jsonArray = JSONArray()
        simulations.forEach { saved ->
            jsonArray.put(encodeSavedSimulation(saved))
        }
        return jsonArray.toString()
    }

    private fun encodeSavedSimulation(saved: SavedSimulation): JSONObject {
        return JSONObject().apply {
            put("id", saved.id)
            put("name", saved.name)
            put("createdAt", saved.createdAt)
            put("lastModified", saved.lastModified)
            put("isFavorite", saved.isFavorite)
            put("tags", JSONArray(saved.tags))
            put("simulation", encodeSimulation(saved.simulation))
            put("result", encodeResult(saved.result))
        }
    }

    private fun decodeSavedSimulation(json: JSONObject): SavedSimulation? {
        val simulationJson = json.optJSONObject("simulation") ?: return null
        val resultJson = json.optJSONObject("result") ?: return null

        val simulation = decodeSimulation(simulationJson) ?: return null
        val result = decodeResult(resultJson) ?: return null

        val tagsJson = json.optJSONArray("tags") ?: JSONArray()
        val tags = buildList {
            for (index in 0 until tagsJson.length()) {
                val tag = tagsJson.optString(index)
                if (tag.isNotBlank()) add(tag)
            }
        }

        return SavedSimulation(
            id = json.optString("id"),
            name = json.optString("name"),
            createdAt = json.optLong("createdAt"),
            lastModified = json.optLong("lastModified"),
            simulation = simulation,
            result = result,
            isFavorite = json.optBoolean("isFavorite"),
            tags = tags
        )
    }

    private fun encodeSimulation(simulation: Simulation): JSONObject {
        val extras = JSONArray().apply {
            simulation.extraAmortizations.forEach { extra ->
                put(
                    JSONObject().apply {
                        put("month", extra.month)
                        put("amount", extra.amount)
                        put("strategy", extra.strategy.name)
                    }
                )
            }
        }

        return JSONObject().apply {
            put("loanAmount", simulation.loanAmount)
            put("interestRate", simulation.interestRate)
            put("rateType", simulation.rateType.name)
            put("terms", simulation.terms)
            put("startDate", simulation.startDate)
            put("amortizationSystem", simulation.amortizationSystem.name)
            put("extraAmortizations", extras)
            put("name", simulation.name)
        }
    }

    private fun decodeSimulation(json: JSONObject): Simulation? {
        val extrasJson = json.optJSONArray("extraAmortizations") ?: JSONArray()
        val extras = buildList {
            for (index in 0 until extrasJson.length()) {
                val extraJson = extrasJson.optJSONObject(index) ?: continue
                val strategy = enumOrNull<ExtraAmortizationStrategy>(extraJson.optString("strategy"))
                    ?: ExtraAmortizationStrategy.REDUCE_TERM
                add(
                    ExtraAmortization(
                        month = extraJson.optInt("month"),
                        amount = extraJson.optDouble("amount"),
                        strategy = strategy
                    )
                )
            }
        }

        val rateType = enumOrNull<InterestRateType>(json.optString("rateType")) ?: InterestRateType.ANNUAL
        val system = enumOrNull<AmortizationSystem>(json.optString("amortizationSystem"))
            ?: AmortizationSystem.SAC

        return Simulation(
            loanAmount = json.optDouble("loanAmount"),
            interestRate = json.optDouble("interestRate"),
            rateType = rateType,
            terms = json.optInt("terms"),
            startDate = json.optString("startDate"),
            amortizationSystem = system,
            extraAmortizations = extras,
            name = json.optString("name")
        )
    }

    private fun encodeResult(result: SavedSimulationResult): JSONObject {
        return JSONObject().apply {
            put("summaryWithoutExtra", encodeSummary(result.summaryWithoutExtra))
            put("summaryWithExtra", encodeSummary(result.summaryWithExtra))
        }
    }

    private fun decodeResult(json: JSONObject): SavedSimulationResult? {
        val withoutJson = json.optJSONObject("summaryWithoutExtra") ?: return null
        val withJson = json.optJSONObject("summaryWithExtra") ?: return null

        val withoutSummary = decodeSummary(withoutJson) ?: return null
        val withSummary = decodeSummary(withJson) ?: return null

        return SavedSimulationResult(
            summaryWithoutExtra = withoutSummary,
            summaryWithExtra = withSummary
        )
    }

    private fun encodeSummary(summary: SimulationSummary): JSONObject {
        return JSONObject().apply {
            put("system", summary.system?.name)
            put("totalPaid", summary.totalPaid)
            put("totalInterest", summary.totalInterest)
            put("totalAmortized", summary.totalAmortized)
            put("totalMonths", summary.totalMonths)
            put("reducedMonths", summary.reducedMonths)
            put("interestSavings", summary.interestSavings)
        }
    }

    private fun decodeSummary(json: JSONObject): SimulationSummary? {
        val system = enumOrNull<AmortizationSystem>(json.optString("system"))

        return SimulationSummary(
            system = system,
            totalPaid = json.optDouble("totalPaid"),
            totalInterest = json.optDouble("totalInterest"),
            totalAmortized = json.optDouble("totalAmortized"),
            totalMonths = json.optInt("totalMonths"),
            reducedMonths = json.optInt("reducedMonths"),
            interestSavings = json.optDouble("interestSavings")
        )
    }

    private inline fun <reified T : Enum<T>> enumOrNull(name: String): T? {
        if (name.isBlank()) return null
        return runCatching { enumValueOf<T>(name) }.getOrNull()
    }

    private companion object {
        const val DATASTORE_NAME = "simulation_history"
        val SIMULATIONS_KEY = stringPreferencesKey("saved_simulations_json")
    }
}
