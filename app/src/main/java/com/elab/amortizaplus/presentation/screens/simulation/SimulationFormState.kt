package com.elab.amortizaplus.presentation.screens.simulation

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.InterestRateType
import com.elab.amortizaplus.presentation.util.percentToDecimal

data class SimulationFormState(
    val loanAmount: String = "",
    val interestRate: String ="",
    val terms: String = "",
    val rateType: InterestRateType = InterestRateType.ANNUAL,
    val system: AmortizationSystem = AmortizationSystem.SAC,

    val loanAmountError: String? = null,
    val interestRateError: String? = null,
    val termsError: String? = null
) {
    fun isValid(): Boolean {
        return loanAmount.isNotBlank() &&
                interestRate.isNotBlank()
                && terms.isNotBlank()
                && loanAmountError == null
                && interestRateError == null
                && termsError == null
    }

    fun toInputData(): SimulationInputData? {
        if (!isValid()) {
            return null
        }

        return try {
            SimulationInputData(
                loanAmount = loanAmount.toDouble(),
                interestRate = interestRate.percentToDecimal() ?: return null,
                rateType = rateType,
                terms = terms.toInt(),
                system = system,
            )
        } catch (e: NumberFormatException) {
            null
        }
    }
}