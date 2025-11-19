package com.elab.amortizaplus.presentation.util

/**
 * Resultado de uma validação de campo.
 * Simplifica a comunicação entre o validador e o ViewModel.
 */
data class ValidationResult(
    val isValid: Boolean,
    val message: String? = null
)