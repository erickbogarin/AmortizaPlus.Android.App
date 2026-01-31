package com.elab.amortizaplus.presentation.screens.simulation.validation

import com.elab.amortizaplus.presentation.util.ValidationResult
import java.time.LocalDate
import java.time.format.DateTimeParseException

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

    fun validateLoanAmount(value: String): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult(false, "Campo obrigatório")
        }

        // valor em centavos!
        val centavos = value.toLongOrNull()
            ?: return ValidationResult(false, "Valor inválido")

        val amount = centavos / 100.0  // agora sim → reais

        return when {
            amount <= 0 -> ValidationResult(false, "Valor deve ser maior que zero")
            amount > 10_000_000 -> ValidationResult(false, "Valor máximo: R$ 10.000.000")
            else -> ValidationResult(true)
        }
    }


    fun validateInterestRate(rawValue: String): ValidationResult {
        if (rawValue.isBlank()) {
            return ValidationResult(false, "Campo obrigatório")
        }

        // rawValue ex.: "1300" → 13.00%
        val basisPoints = rawValue.toLongOrNull()
            ?: return ValidationResult(false, "Taxa inválida")

        val rate = basisPoints / 100.0  // agora sim → 13.00

        return when {
            rate <= 0 -> ValidationResult(false, "Taxa deve ser maior que zero")
            rate > 30 -> ValidationResult(false, "Taxa máxima: 30%")
            else -> ValidationResult(true)
        }
    }

    fun validateTerms(value: String): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult(false, "Campo obrigatório")
        }

        val terms = value.toIntOrNull()
            ?: return ValidationResult(false, "Prazo inválido")

        return when {
            terms <= 0 -> ValidationResult(false, "Prazo deve ser maior que zero")
            terms > 600 -> ValidationResult(false, "Prazo máximo: 600 meses (50 anos)")
            else -> ValidationResult(true)
        }
    }

    fun validateStartDate(value: String): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult(false, "Campo obrigatório")
        }

        return try {
            LocalDate.parse(value)
            ValidationResult(true)
        } catch (e: DateTimeParseException) {
            ValidationResult(false, "Data inválida (use AAAA-MM-DD)")
        }
    }
}
