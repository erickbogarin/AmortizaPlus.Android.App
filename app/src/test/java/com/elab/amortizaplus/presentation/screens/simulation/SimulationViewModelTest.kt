package com.elab.amortizaplus.presentation.screens.simulation

import com.elab.amortizaplus.domain.calculator.FinancingCalculator
import com.elab.amortizaplus.domain.calculator.PriceCalculator
import com.elab.amortizaplus.domain.calculator.SacCalculator
import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.SavedSimulation
import com.elab.amortizaplus.domain.model.SavedSimulationResult
import com.elab.amortizaplus.domain.model.Simulation
import com.elab.amortizaplus.domain.model.SimulationSummary
import com.elab.amortizaplus.domain.usecase.CalculateFinancingUseCase
import com.elab.amortizaplus.domain.usecase.FakeSimulationHistoryRepository
import com.elab.amortizaplus.domain.usecase.GetSimulationByIdUseCase
import com.elab.amortizaplus.domain.usecase.SaveSimulationUseCase
import com.elab.amortizaplus.presentation.MainDispatcherRule
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import com.elab.amortizaplus.presentation.screens.simulation.validation.SimulationInputValidator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimulationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val validator = SimulationInputValidator()

    @Test
    fun `calculate should auto save simulation on success`() = runTest {
        val deps = buildViewModel()
        val viewModel = deps.viewModel

        viewModel.onLoanAmountChange("25000000")
        viewModel.onInterestRateChange("1300")
        viewModel.onTermsChange("360")
        viewModel.onStartDateChange("022026")
        viewModel.calculate()

        advanceTimeBy(500)
        advanceUntilIdle()

        val saved = deps.repository.getAllSimulations().getOrNull().orEmpty()
        assertEquals(1, saved.size)
        assertTrue(viewModel.uiState.value is SimulationUiState.Success)
    }

    @Test
    fun `loadSavedSimulation should map domain model to raw form values`() = runTest {
        val deps = buildViewModel()
        val viewModel = deps.viewModel
        val saved = SavedSimulation(
            id = "saved-1",
            name = "Teste",
            createdAt = 1L,
            lastModified = 2L,
            simulation = Simulation(
                loanAmount = 250_000.0,
                interestRate = 0.13,
                terms = 360,
                startDate = "2026-02",
                amortizationSystem = AmortizationSystem.SAC,
                extraAmortizations = emptyList(),
                name = "Teste"
            ),
            result = SavedSimulationResult(
                summaryWithoutExtra = SimulationSummary(
                    totalPaid = 0.0,
                    totalInterest = 0.0,
                    totalAmortized = 0.0,
                    totalMonths = 0
                ),
                summaryWithExtra = SimulationSummary(
                    totalPaid = 0.0,
                    totalInterest = 0.0,
                    totalAmortized = 0.0,
                    totalMonths = 0
                )
            )
        )
        deps.repository.saveSimulation(saved)

        viewModel.loadSavedSimulation("saved-1")
        advanceUntilIdle()

        val state = viewModel.formState.value
        assertEquals("25000000", state.loanAmount)
        assertEquals("1300", state.interestRate)
        assertEquals("360", state.terms)
        assertEquals("022026", state.startDate)
        assertTrue(viewModel.uiState.value is SimulationUiState.Success)
    }

    @Test
    fun `loadSavedSimulation should set error when id does not exist`() = runTest {
        val deps = buildViewModel()
        val viewModel = deps.viewModel

        viewModel.loadSavedSimulation("not-found")
        advanceUntilIdle()

        val uiState = viewModel.uiState.value as SimulationUiState.Error
        assertEquals(SimulationTexts.historyNotFound, uiState.message)
    }

    private fun buildViewModel(): TestDeps {
        val repository = FakeSimulationHistoryRepository()
        val calculateUseCase = CalculateFinancingUseCase(
            financingCalculator = FinancingCalculator(
                sacCalculator = SacCalculator(),
                priceCalculator = PriceCalculator()
            )
        )
        val saveUseCase = SaveSimulationUseCase(repository)
        val getByIdUseCase = GetSimulationByIdUseCase(repository)

        val viewModel = SimulationViewModel(
            calculateFinancingUseCase = calculateUseCase,
            validator = validator,
            saveSimulationUseCase = saveUseCase,
            getSimulationByIdUseCase = getByIdUseCase
        )
        return TestDeps(viewModel = viewModel, repository = repository)
    }

private data class TestDeps(
        val viewModel: SimulationViewModel,
        val repository: FakeSimulationHistoryRepository
    )
}
