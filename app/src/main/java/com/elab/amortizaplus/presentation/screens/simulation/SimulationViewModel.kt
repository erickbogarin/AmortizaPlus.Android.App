package com.elab.amortizaplus.presentation.screens.simulation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elab.amortizaplus.domain.model.InterestRateType
import com.elab.amortizaplus.domain.model.Simulation
import com.elab.amortizaplus.domain.usecase.CalculateFinancingUseCase
import com.elab.amortizaplus.domain.usecase.GetSimulationByIdUseCase
import com.elab.amortizaplus.domain.usecase.SaveSimulationUseCase
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import com.elab.amortizaplus.presentation.screens.simulation.validation.SimulationInputValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

class SimulationViewModel(
    private val calculateFinancingUseCase: CalculateFinancingUseCase,
    private val validator: SimulationInputValidator,
    private val saveSimulationUseCase: SaveSimulationUseCase,
    private val getSimulationByIdUseCase: GetSimulationByIdUseCase
): ViewModel() {
    private val _formState = MutableStateFlow(SimulationFormState())

    val formState: StateFlow<SimulationFormState> = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<SimulationUiState>(SimulationUiState.Initial)

    val uiState: StateFlow<SimulationUiState> = _uiState.asStateFlow()

    // -------------------------------------------------------------------------
    // Ações do formulário
    // -------------------------------------------------------------------------

    fun onLoanAmountChange(value: String) {
        val validation = validator.validateLoanAmount(value)
        _formState.value = _formState.value.copy(
            loanAmount = value,
            loanAmountError = validation.message
        )
    }

    fun onInterestRateChange(value: String) {
        val validation = validator.validateInterestRate(value)
        _formState.value = _formState.value.copy(
            interestRate = value,
            interestRateError = validation.message
        )
    }

    fun onTermsChange(value: String) {
        val validation = validator.validateTerms(value)
        _formState.value = validateExtras(
            _formState.value.copy(
                terms = value,
                termsError = validation.message
            )
        )
    }

    fun onStartDateChange(value: String) {
        val validation = validator.validateStartDate(value)
        _formState.value = _formState.value.copy(
            startDate = value,
            startDateError = validation.message
        )
    }

    fun onRateTypeChange(rateType: InterestRateType) {
        _formState.value = _formState.value.copy(rateType = rateType)
    }

    fun onSystemChange(system: com.elab.amortizaplus.domain.model.AmortizationSystem) {
        _formState.value = _formState.value.copy(system = system)
    }

    fun addExtraAmortization() {
        val newItem = ExtraAmortizationFormItem(
            id = System.currentTimeMillis()
        )
        _formState.value = validateExtras(
            _formState.value.copy(
                extraAmortizations = _formState.value.extraAmortizations + newItem
            )
        )
    }

    fun removeExtraAmortization(id: Long) {
        _formState.value = validateExtras(
            _formState.value.copy(
                extraAmortizations = _formState.value.extraAmortizations.filterNot { it.id == id }
            )
        )
    }

    fun onExtraMonthChange(id: Long, value: String) {
        updateExtraItem(id) { it.copy(month = value) }
    }

    fun onExtraAmountChange(id: Long, value: String) {
        updateExtraItem(id) { it.copy(amount = value) }
    }

    fun onExtraStrategyChange(id: Long, strategy: com.elab.amortizaplus.domain.model.ExtraAmortizationStrategy) {
        updateExtraItem(id) { it.copy(strategy = strategy) }
    }

    // -------------------------------------------------------------------------
    // Execução da simulação
    // -------------------------------------------------------------------------

    fun calculate() {
        if (!_formState.value.isValid()) {
            _uiState.value = SimulationUiState.Error(SimulationTexts.formInvalidMessage)
            return
        }

        val inputData = _formState.value.toInputData()
        if (inputData == null) {
            _uiState.value = SimulationUiState.Error(SimulationTexts.inputProcessingError)
            return
        }

        viewModelScope.launch {
            _uiState.value = SimulationUiState.Loading
            try {
                delay(400)
                val simulation = Simulation(
                    loanAmount = inputData.loanAmount,
                    interestRate = inputData.interestRate,
                    rateType = inputData.rateType,
                    terms = inputData.terms,
                    startDate = inputData.startDate,
                    amortizationSystem = inputData.system,
                    extraAmortizations = inputData.extraAmortizations,
                    name = "${SimulationTexts.simulationNamePrefix} ${System.currentTimeMillis()}"
                )

                val result = calculateFinancingUseCase(simulation)
                _uiState.value = SimulationUiState.Success(
                    inputData = inputData,
                    summaryWithout = result.summaryWithoutExtra,
                    summaryWith = result.summaryWithExtra,
                    installmentsWithout = result.paymentsWithoutExtra,
                    installmentsWith = result.paymentsWithExtra
                )

                // Persistência assíncrona: não bloqueia nem altera sucesso do cálculo.
                saveSimulationUseCase(
                    simulation = simulation,
                    result = result
                )
            } catch (e: Exception) {
                _uiState.value = SimulationUiState.Error(
                    "${SimulationTexts.calculationErrorPrefix} ${e.message}"
                )
            }
        }
    }

    fun loadSavedSimulation(id: String) {
        if (id.isBlank()) return

        viewModelScope.launch {
            _uiState.value = SimulationUiState.Loading
            val loadResult = getSimulationByIdUseCase(id)

            loadResult.fold(
                onSuccess = { saved ->
                    if (saved == null) {
                        _uiState.value = SimulationUiState.Error(SimulationTexts.historyNotFound)
                        return@fold
                    }

                    _formState.value = buildFormStateFromSimulation(saved.simulation)

                    try {
                        val calculated = calculateFinancingUseCase(saved.simulation)
                        _uiState.value = SimulationUiState.Success(
                            inputData = saved.simulation.toInputData(),
                            summaryWithout = calculated.summaryWithoutExtra,
                            summaryWith = calculated.summaryWithExtra,
                            installmentsWithout = calculated.paymentsWithoutExtra,
                            installmentsWith = calculated.paymentsWithExtra
                        )
                    } catch (e: Exception) {
                        _uiState.value = SimulationUiState.Error(
                            "${SimulationTexts.calculationErrorPrefix} ${e.message}"
                        )
                    }
                },
                onFailure = {
                    _uiState.value = SimulationUiState.Error(SimulationTexts.historyLoadError)
                }
            )
        }
    }

    fun reset() {
        _formState.value = SimulationFormState()
        _uiState.value = SimulationUiState.Initial
    }

    private fun updateExtraItem(
        id: Long,
        transform: (ExtraAmortizationFormItem) -> ExtraAmortizationFormItem
    ) {
        _formState.value = validateExtras(
            _formState.value.copy(
                extraAmortizations = _formState.value.extraAmortizations.map { item ->
                    if (item.id == id) transform(item) else item
                }
            )
        )
    }

    private fun buildFormStateFromSimulation(simulation: Simulation): SimulationFormState {
        val loadedExtras = simulation.extraAmortizations.mapIndexed { index, extra ->
            ExtraAmortizationFormItem(
                id = System.currentTimeMillis() + index,
                month = extra.month.toString(),
                amount = (extra.amount * 100).roundToLong().toString(),
                strategy = extra.strategy
            )
        }

        val rawLoanAmount = (simulation.loanAmount * 100).roundToLong().toString()
        val rawInterestRate = (simulation.interestRate * 10_000).roundToLong().toString()
        val rawTerms = simulation.terms.toString()
        val rawStartDate = simulation.startDate.toRawMonthYear()

        val state = SimulationFormState(
            loanAmount = rawLoanAmount,
            interestRate = rawInterestRate,
            terms = rawTerms,
            startDate = rawStartDate,
            rateType = simulation.rateType,
            system = simulation.amortizationSystem,
            extraAmortizations = loadedExtras,
            loanAmountError = validator.validateLoanAmount(rawLoanAmount).message,
            interestRateError = validator.validateInterestRate(rawInterestRate).message,
            termsError = validator.validateTerms(rawTerms).message,
            startDateError = validator.validateStartDate(rawStartDate).message
        )

        return validateExtras(state)
    }

    private fun Simulation.toInputData(): SimulationInputData {
        return SimulationInputData(
            loanAmount = loanAmount,
            interestRate = interestRate,
            rateType = rateType,
            terms = terms,
            system = amortizationSystem,
            startDate = startDate,
            extraAmortizations = extraAmortizations
        )
    }

    private fun String.toRawMonthYear(): String {
        val parts = split("-")
        if (parts.size == 2 && parts[0].length == 4) {
            val year = parts[0]
            val month = parts[1].padStart(2, '0')
            return "$month$year"
        }

        val digits = filter { it.isDigit() }
        if (digits.length == 6) {
            return digits
        }

        return this
    }

    private fun validateExtras(state: SimulationFormState): SimulationFormState {
        val termsValue = state.terms.toIntOrNull()
        val termsLimit = if (termsValue != null && termsValue > 0) termsValue else 0

        val monthCounts = state.extraAmortizations
            .mapNotNull { it.month.toIntOrNull() }
            .groupingBy { it }
            .eachCount()

        val validated = state.extraAmortizations.map { item ->
            val monthRaw = item.month
            val amountRaw = item.amount

            val hasMonth = monthRaw.isNotBlank()
            val hasAmount = amountRaw.isNotBlank()

            val (monthError, amountError) = if (!hasMonth && !hasAmount) {
                null to null
            } else {
                var computedMonthError = if (!hasMonth) {
                    validator.validateExtraMonth(monthRaw, termsLimit).message
                } else if (termsLimit == 0) {
                    SimulationTexts.extraMonthInvalid
                } else {
                    validator.validateExtraMonth(monthRaw, termsLimit).message
                }

                val computedAmountError = validator.validateExtraAmount(amountRaw).message

                val monthValue = monthRaw.toIntOrNull()
                if (monthValue != null && (monthCounts[monthValue] ?: 0) > 1) {
                    computedMonthError = SimulationTexts.extraMonthDuplicate
                }

                computedMonthError to computedAmountError
            }

            item.copy(
                monthError = monthError,
                amountError = amountError
            )
        }

        return state.copy(extraAmortizations = validated)
    }
}
