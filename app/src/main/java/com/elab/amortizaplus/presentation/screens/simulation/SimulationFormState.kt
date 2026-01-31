package com.elab.amortizaplus.presentation.screens.simulation

import com.elab.amortizaplus.domain.model.AmortizationSystem
import com.elab.amortizaplus.domain.model.ExtraAmortization
import com.elab.amortizaplus.domain.model.ExtraAmortizationStrategy
import com.elab.amortizaplus.domain.model.InterestRateType
import com.elab.amortizaplus.domain.util.DateProvider

data class SimulationFormState(
    val loanAmount: String = "",
    val interestRate: String ="",
    val terms: String = "",
    val startDate: String = DateProvider.monthYear(),
    val rateType: InterestRateType = InterestRateType.ANNUAL,
    val system: AmortizationSystem = AmortizationSystem.SAC,
    val extraAmortizations: List<ExtraAmortizationFormItem> = emptyList(),

    val loanAmountError: String? = null,
    val interestRateError: String? = null,
    val termsError: String? = null,
    val startDateError: String? = null
) {
    fun isValid(): Boolean {
        return loanAmount.isNotBlank() &&
                interestRate.isNotBlank()
                && terms.isNotBlank()
                && startDate.isNotBlank()
                && loanAmountError == null
                && interestRateError == null
                && termsError == null
                && startDateError == null
    }

    fun toInputData(): SimulationInputData? {
        if (!isValid()) {
            return null
        }

        return try {
            val loanAmountValue = loanAmount.toLongOrNull() ?: return null
            val basisPoints = interestRate.toLongOrNull() ?: return null
            val startDateRaw = startDate
            if (startDateRaw.length != 6) return null
            val startDateFormatted = "${startDateRaw.substring(2, 6)}-${startDateRaw.substring(0, 2)}"
            val parsedExtras = extraAmortizations
                .mapNotNull { item ->
                    val month = item.month.toIntOrNull() ?: return@mapNotNull null
                    val amountCents = item.amount.toLongOrNull() ?: return@mapNotNull null
                    if (month <= 0 || amountCents <= 0) return@mapNotNull null
                    ExtraAmortization(
                        month = month,
                        amount = amountCents / 100.0,
                        strategy = item.strategy
                    )
                }
            SimulationInputData(
                loanAmount = loanAmountValue / 100.0,
                interestRate = basisPoints / 10000.0,
                rateType = rateType,
                terms = terms.toInt(),
                system = system,
                startDate = startDateFormatted,
                extraAmortizations = parsedExtras
            )
        } catch (e: NumberFormatException) {
            null
        }
    }
}

data class ExtraAmortizationFormItem(
    val id: Long,
    val month: String = "",
    val amount: String = "",
    val strategy: ExtraAmortizationStrategy = ExtraAmortizationStrategy.REDUCE_TERM
)
