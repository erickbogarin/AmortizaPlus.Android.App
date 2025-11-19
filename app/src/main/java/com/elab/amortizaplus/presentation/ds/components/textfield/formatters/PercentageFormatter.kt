package com.elab.amortizaplus.presentation.ds.components.textfield.formatters

import com.elab.amortizaplus.presentation.ds.components.textfield.formatters.InputSanitizers.digitsAndDecimal
import com.elab.amortizaplus.presentation.ds.components.textfield.formatters.LocaleUtils.decimalSeparator
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Formatter para porcentagens com decimais.
 *
 * Armazenamento: Basis points (ex: 12.5% = 1250)
 *
 * Fluxo:
 * 1. User digita: "12,5"
 * 2. Sanitize: "12,5" (aceita decimal)
 * 3. Parse: "1250" (basis points)
 * 4. Display: "12,50%"
 *
 * ✅ Formatação unificada em formatForDisplay()
 */
class PercentageFormatter(
    private val locale: Locale = Locale("pt", "BR"),
    private val maxDecimalPlaces: Int = 2,
    private val maxIntegerDigits: Int = 3  // 0-100 (ou até 999 se necessário)
) : InputFormatter {

    private val separator = locale.decimalSeparator()

    override fun sanitize(input: String): String {
        // Aceita dígitos + um único separador decimal
        val withDecimal = digitsAndDecimal(
            input = input,
            decimalSeparator = separator,
            allowMultiple = false
        )

        // ✅ REFINAMENTO: Remove zeros à esquerda desnecessários
        val cleaned = removeLeadingZeros(withDecimal)

        // Valida limites (ex: max 3 dígitos antes, 2 depois)
        return limitDecimalPlaces(cleaned)
    }

    /**
     * Remove zeros à esquerda desnecessários.
     * "000" → "0"
     * "00123" → "123"
     * "0,5" → "0,5" (mantém se houver decimal)
     */
    private fun removeLeadingZeros(input: String): String {
        if (input.isBlank() || input == "0") return input
        if (input.startsWith("0${separator}")) return input  // "0,5" é válido

        // Remove zeros à esquerda: "00123" → "123"
        val trimmed = input.trimStart('0')

        // Se ficou vazio ou só separador, retorna "0"
        return trimmed.ifBlank { "0" }
    }

    override fun parse(input: String): String {
        // Remove separador e converte para basis points
        // Ex: "12,5" → "1250" (12.5 * 100)

        if (input.isBlank()) return "0"

        val parts = input.split(separator)
        val integerPart = parts.getOrNull(0)?.ifBlank { "0" } ?: "0"
        val decimalPart = parts.getOrNull(1)?.padEnd(maxDecimalPlaces, '0') ?: "0".repeat(maxDecimalPlaces)

        // ✅ ROBUSTEZ: Garante padding correto para valores pequenos
        // "1" → "100" (1.00%)
        // "12" → "1200" (12.00%)
        // "0,5" → "50" (0.50%)
        val combined = integerPart + decimalPart.take(maxDecimalPlaces)

        return combined.toLongOrNull()?.toString() ?: "0"
    }

    override fun formatForDisplay(rawValue: String): String {
        if (rawValue.isBlank()) return ""

        // ✅ CONSISTÊNCIA: Exibe zero explicitamente (como Money)
        val value = rawValue.toLongOrNull() ?: return ""
        if (value == 0L) return "0,00%"

        // Reconstrói decimal a partir de basis points
        // "1250" → "12,50%"

        // Divide por 10^maxDecimalPlaces
        val divisor = Math.pow(10.0, maxDecimalPlaces.toDouble())
        val decimal = value / divisor

        // Formata com casas decimais
        val formatted = String.format(locale, "%.${maxDecimalPlaces}f", decimal)
            .replace('.', separator)

        return "$formatted%"
    }

    /**
     * Limita dígitos antes e depois do separador.
     */
    private fun limitDecimalPlaces(input: String): String {
        if (!input.contains(separator)) {
            return input.take(maxIntegerDigits)
        }
        val parts = input.split(separator)
        val integerPart = parts[0].take(maxIntegerDigits)
        val decimalPart = parts[1].take(maxDecimalPlaces)
        return "$integerPart$separator$decimalPart"
    }
}