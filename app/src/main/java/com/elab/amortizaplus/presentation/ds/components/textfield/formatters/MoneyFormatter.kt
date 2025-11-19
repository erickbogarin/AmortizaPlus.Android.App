package com.elab.amortizaplus.presentation.ds.components.textfield.formatters

import androidx.compose.ui.text.input.VisualTransformation
import com.elab.amortizaplus.presentation.ds.components.textfield.formatters.InputSanitizers.digitsOnly
import com.elab.amortizaplus.presentation.ds.components.textfield.formatters.InputSanitizers.maxLength
import java.text.NumberFormat
import java.util.Locale

// ========================================
// 3. MONEY FORMATTER (refatorado)
// ========================================

/**
 * Formatter para valores monetários (Real brasileiro).
 *
 * Armazenamento: Centavos (Long) para evitar problemas com decimais
 *
 * Fluxo:
 * 1. User digita: "12345"
 * 2. Raw value: "12345" (= R$ 123,45)
 * 3. Display: "R$ 123,45"
 *
 * ✅ Formatação 100% em formatForDisplay() (não na transformation)
 * ✅ NumberFormat memoizado para performance
 */
class MoneyFormatter(
    private val locale: Locale = Locale("pt", "BR"),
    private val maxDigits: Int = 15  // ~R$ 10 trilhões
) : InputFormatter {

    companion object {
        // ✅ OTIMIZAÇÃO: Cache de formatters por locale
        private val formatterCache = mutableMapOf<Locale, NumberFormat>()

        private fun getCurrencyFormatter(locale: Locale): NumberFormat {
            return formatterCache.getOrPut(locale) {
                NumberFormat.getCurrencyInstance(locale)
            }
        }
    }

    private val currencyFormatter = getCurrencyFormatter(locale)

    override fun sanitize(input: String): String =
        maxLength(digitsOnly(input), maxDigits)

    override fun formatForDisplay(rawValue: String): String {
        if (rawValue.isBlank()) return ""

        // Converte centavos → reais
        val centavos = rawValue.toLongOrNull() ?: return ""
        val reais = centavos.toDouble() / 100

        // Formatação manualmente controlada (seguro)
        val formatted = String.format(locale, "%,.2f", reais)
            .replace('.', '#')      // troca ponto temporário
            .replace(',', '.')      // troca vírgula decimal
            .replace('#', ',')      // restaura vírgula
        return "R$ $formatted"
    }

    override fun createTransformation(): VisualTransformation =
        SimpleCursorTransformation()
}