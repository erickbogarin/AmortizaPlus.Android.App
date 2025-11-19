package com.elab.amortizaplus.presentation.screens.simulation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elab.amortizaplus.domain.model.InterestRateType
import com.elab.amortizaplus.domain.model.Simulation
import com.elab.amortizaplus.domain.usecase.CalculateFinancingUseCase
import com.elab.amortizaplus.domain.util.DateProvider
import com.elab.amortizaplus.presentation.screens.simulation.validation.SimulationInputValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        val filtered = value.filter { it.isDigit() || it == '.' || it == ',' }
        val validation = validator.validateLoanAmount(filtered)
        _formState.value = _formState.value.copy(
            loanAmount = filtered,
            loanAmountError = validation.message
        )
    }

    fun onInterestRateChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' || it == ',' }
        val validation = validator.validateInterestRate(filtered)
        _formState.value = _formState.value.copy(
            interestRate = filtered,
            interestRateError = validation.message
        )
    }

    fun onTermsChange(value: String) {
        val filtered = value.filter { it.isDigit() }
        val validation = validator.validateTerms(filtered)
        _formState.value = _formState.value.copy(
            terms = filtered,
            termsError = validation.message
        )
    }

    fun onRateTypeChange(rateType: InterestRateType) {
        _formState.value = _formState.value.copy(rateType = rateType)
    }

    fun onSystemChange(system: com.elab.amortizaplus.domain.model.AmortizationSystem) {
        _formState.value = _formState.value.copy(system = system)
    }

    // -------------------------------------------------------------------------
    // Execução da simulação
    // -------------------------------------------------------------------------

    fun calculate() {
        if (!_formState.value.isValid()) {
            _uiState.value = SimulationUiState.Error("Por favor, preencha todos os campos corretamente")
            return
        }

        val inputData = _formState.value.toInputData()
        if (inputData == null) {
            _uiState.value = SimulationUiState.Error("Erro ao processar dados de entrada")
            return
        }

        viewModelScope.launch {
            _uiState.value = SimulationUiState.Loading
            try {
                val simulation = Simulation(
                    loanAmount = inputData.loanAmount,
                    interestRate = inputData.interestRate,
                    rateType = inputData.rateType,
                    terms = inputData.terms,
                    startDate = DateProvider.today(),
                    amortizationSystem = inputData.system,
                    extraAmortizations = emptyList(),
                    name = "Simulação ${System.currentTimeMillis()}"
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
                _uiState.value = SimulationUiState.Error("Erro ao calcular: ${e.message}")
            }
        }
    }

    fun reset() {
        _formState.value = SimulationFormState()
        _uiState.value = SimulationUiState.Initial
    }
}
