package com.elab.amortizaplus.presentation.ds.components.textfield.formatters

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Formatter para mês/ano em formato MM/AAAA.
 *
 * Raw: "022026"
 * Display: "02/2026"
 */
class MonthYearFormatter : InputFormatter {

    override fun sanitize(input: String): String {
        val digits = input.filter { it.isDigit() }
        return digits.take(6)
    }

    override fun parse(input: String): String = input

    override fun createTransformation(): VisualTransformation =
        MonthYearVisualTransformation()
}

private class MonthYearVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val formatted = when {
            raw.isBlank() -> ""
            raw.length <= 2 -> raw
            else -> raw.substring(0, 2) + "/" + raw.substring(2)
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = formatted.length
            override fun transformedToOriginal(offset: Int): Int = raw.length
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
