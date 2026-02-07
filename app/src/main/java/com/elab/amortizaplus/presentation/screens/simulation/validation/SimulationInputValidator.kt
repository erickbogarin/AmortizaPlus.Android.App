package com.elab.amortizaplus.presentation.screens.simulation.validation

import com.elab.amortizaplus.presentation.util.ValidationResult
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import java.time.YearMonth

/**
 * Responsável por validar os campos do formulário de simulação.
 *
 * Essa camada atua antes do domínio, garantindo que a entrada do usuário
 * seja coerente e evitando cálculos com valores inválidos.
 *
 * Exemplo:
 * - Campos vazios → erro de preenchimento.
 * - Taxa de juros fora do intervalo 0.1–30% → erro de validação.
 * - Prazo fora de 1–600 meses → erro de validação.
 */
class SimulationInputValidator {
    companion object {
        const val MIN_TERMS = 1
        const val MAX_TERMS = 600
    }

    fun validateLoanAmount(value: String): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult(false, SimulationTexts.requiredField)
        }

        // valor em centavos!
        val centavos = value.toLongOrNull()
            ?: return ValidationResult(false, SimulationTexts.invalidValue)

        val amount = centavos / 100.0  // agora sim → reais

        return when {
            amount <= 0 -> ValidationResult(false, SimulationTexts.valueMustBeGreaterThanZero)
            amount > 10_000_000 -> ValidationResult(false, SimulationTexts.loanAmountMax)
            else -> ValidationResult(true)
        }
    }


    fun validateInterestRate(rawValue: String): ValidationResult {
        if (rawValue.isBlank()) {
            return ValidationResult(false, SimulationTexts.requiredField)
        }

        // rawValue ex.: "1300" → 13.00%
        val basisPoints = rawValue.toLongOrNull()
            ?: return ValidationResult(false, SimulationTexts.invalidRate)

        val rate = basisPoints / 100.0  // agora sim → 13.00

        return when {
            rate <= 0 -> ValidationResult(false, SimulationTexts.rateMustBeGreaterThanZero)
            rate > 30 -> ValidationResult(false, SimulationTexts.maxRate)
            else -> ValidationResult(true)
        }
    }

    fun validateTerms(value: String): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult(false, SimulationTexts.requiredField)
        }

        val terms = value.toIntOrNull()
            ?: return ValidationResult(false, SimulationTexts.invalidTerms)

        return when {
            terms < MIN_TERMS -> ValidationResult(false, SimulationTexts.termsMustBeGreaterThanZero)
            terms > MAX_TERMS -> ValidationResult(false, SimulationTexts.maxTerms)
            else -> ValidationResult(true)
        }
    }

    fun validateStartDate(value: String): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult(false, SimulationTexts.requiredField)
        }

        val raw = value.filter { it.isDigit() }
        if (raw.length != 6) {
            return ValidationResult(false, SimulationTexts.invalidDate)
        }

        return try {
            val month = raw.substring(0, 2).toInt()
            val year = raw.substring(2, 6).toInt()
            YearMonth.of(year, month)
            ValidationResult(true)
        } catch (e: Exception) {
            ValidationResult(false, SimulationTexts.invalidDate)
        }
    }

    fun validateExtraMonth(value: String, terms: Int): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult(false, SimulationTexts.extraMonthRequired)
        }

        val month = value.toIntOrNull()
            ?: return ValidationResult(false, SimulationTexts.extraMonthInvalid)

        return when {
            month <= 0 -> ValidationResult(false, SimulationTexts.extraMonthTooLow)
            month > terms -> ValidationResult(false, "${SimulationTexts.extraMonthTooHighPrefix} $terms")
            else -> ValidationResult(true)
        }
    }

    fun validateExtraAmount(value: String): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult(false, SimulationTexts.extraAmountRequired)
        }

        val cents = value.toLongOrNull()
            ?: return ValidationResult(false, SimulationTexts.extraAmountInvalid)

        return when {
            cents <= 0 -> ValidationResult(false, SimulationTexts.extraAmountTooLow)
            else -> ValidationResult(true)
        }
    }
}
