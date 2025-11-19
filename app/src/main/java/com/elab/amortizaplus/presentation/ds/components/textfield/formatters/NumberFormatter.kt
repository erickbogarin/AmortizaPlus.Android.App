package com.elab.amortizaplus.presentation.ds.components.textfield.formatters

import com.elab.amortizaplus.presentation.ds.components.textfield.formatters.InputSanitizers.digitsOnly
import com.elab.amortizaplus.presentation.ds.components.textfield.formatters.InputSanitizers.maxLength

// ========================================
// 2. NUMBER FORMATTER
// ========================================

/**
 * Formatter para números inteiros.
 *
 * Input:  "123abc456"
 * Sanitize: "123456"
 * Raw:    "123456"
 * Display: "123456" (sem formatação)
 */
class NumberFormatter(
    private val maxDigits: Int = 10
) : InputFormatter {

    override fun sanitize(input: String): String =
        maxLength(digitsOnly(input), maxDigits)

    // Número não precisa formatação visual
    override fun formatForDisplay(rawValue: String): String = rawValue
}
