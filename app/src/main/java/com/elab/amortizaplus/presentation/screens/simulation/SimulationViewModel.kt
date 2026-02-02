package com.elab.amortizaplus.presentation.screens.simulation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elab.amortizaplus.domain.model.InterestRateType
import com.elab.amortizaplus.domain.model.Simulation
import com.elab.amortizaplus.domain.usecase.CalculateFinancingUseCase
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import com.elab.amortizaplus.presentation.screens.simulation.validation.SimulationInputValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SimulationViewModel(
    private val calculateFinancingUseCase: CalculateFinancingUseCase,
    private val validator: SimulationInputValidator,

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
            } catch (e: Exception) {
                _uiState.value = SimulationUiState.Error(
                    "${SimulationTexts.calculationErrorPrefix} ${e.message}"
                )
            }
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
