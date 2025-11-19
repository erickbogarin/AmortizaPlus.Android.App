package com.elab.amortizaplus.presentation.ds.components.textfield.formatters

import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

class MoneyFormatter(
    private val locale: Locale = Locale("pt", "BR"),
    private val maxDigits: Int = 15
) : InputFormatter {

    companion object {

        private val formatterCache = mutableMapOf<Locale, NumberFormat>()

        private fun getCurrencyFormatter(locale: Locale): NumberFormat {
            val base = formatterCache.getOrPut(locale) {
                NumberFormat.getCurrencyInstance(locale).apply {
                    maximumFractionDigits = 2
                    minimumFractionDigits = 2
                }
            }

            return (base.clone() as NumberFormat)
        }
    }


    override fun sanitize(input: String): String {
        val digits = input.filter { it.isDigit() }
        return digits.take(maxDigits)
    }

    override fun formatForDisplay(rawValue: String): String {
        if (rawValue.isBlank()) return ""

        val centavos = rawValue.toLongOrNull() ?: return ""
        val reais = centavos / 100.0

        return getCurrencyFormatter(locale).format(reais)
    }

    override fun createTransformation(): VisualTransformation =
        SimpleCursorTransformation()
}
