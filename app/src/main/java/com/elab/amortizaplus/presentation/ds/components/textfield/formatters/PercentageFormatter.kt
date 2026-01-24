package com.elab.amortizaplus.presentation.ds.components.textfield.formatters

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.util.Locale

/**
 * ✅ SOLUÇÃO: Percentage Formatter com arquitetura consistente
 *
 * Raw value = basis points sem formatação (ex: "1234" = 12.34%)
 * Input = apenas dígitos (sem vírgula, sem %)
 * Display = formatado com vírgula e % (ex: "12,34%")
 *
 * Similar ao MoneyFormatter:
 * - TextField usa raw value
 * - VisualTransformation adiciona formatação visual
 * - Sem problemas de concatenação
 */
class PercentageFormatter(
    private val locale: Locale = Locale("pt", "BR"),
    private val maxDecimalPlaces: Int = 2,
    private val maxIntegerDigits: Int = 3
) : InputFormatter {

    private val separator = if (locale.language == "pt") ',' else '.'
    private val maxDigits = maxIntegerDigits + maxDecimalPlaces // ex: 3 + 2 = 5 dígitos (999,99%)

    // ------------------------------------------------------------
    // 1) SANITIZE - Remove tudo exceto dígitos
    // ------------------------------------------------------------
    override fun sanitize(input: String): String {
        // Remove TUDO exceto dígitos (%, vírgula, pontos, letras)
        val digitsOnly = input.filter { it.isDigit() }

        // Remove zeros à esquerda
        val trimmed = digitsOnly.trimStart('0')

        // Limita ao máximo de dígitos permitido
        val limited = trimmed.take(maxDigits)

        return limited.ifBlank { "0" }
    }

    // ------------------------------------------------------------
    // 2) PARSE - Já vem sanitizado, retorna como está
    // ------------------------------------------------------------
    override fun parse(input: String): String {
        // Input já vem sanitizado (apenas dígitos)
        // "123" → "123" (representa 1,23%)
        return if (input.isBlank()) "0" else input
    }

    // ------------------------------------------------------------
    // 3) DISPLAY - Formata basis points para percentual com %
    // ------------------------------------------------------------
    override fun formatForDisplay(rawValue: String): String {
        if (rawValue.isBlank() || rawValue == "0") {
            return "0${separator}00%"
        }

        val basisPoints = rawValue.toLongOrNull() ?: return "0${separator}00%"

        // Converte basis points para percentual
        // Ex: 1234 basis points = 12.34%
        val divisor = Math.pow(10.0, maxDecimalPlaces.toDouble())
        val percent = basisPoints / divisor

        // Formata com 2 casas decimais
        val formatted = String.format(locale, "%.${maxDecimalPlaces}f%%", percent)

        // Substitui ponto por vírgula se necessário
        return formatted.replace('.', separator)
    }

    // ------------------------------------------------------------
    // 4) TRANSFORMATION - Formata visualmente enquanto digita
    // ------------------------------------------------------------
    override fun createTransformation(): VisualTransformation {
        return PercentageVisualTransformation(separator, maxDecimalPlaces)
    }
}

/**
 * VisualTransformation para campo de porcentagem.
 * Transforma "1234" → "12,34%" enquanto o usuário digita.
 */
class PercentageVisualTransformation(
    private val separator: Char = ',',
    private val decimalPlaces: Int = 2
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text

        // Formata os dígitos raw para percentual visual
        val formatted = formatToPercentage(original)

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = PercentageOffsetMapping(
                originalLength = original.length,
                formattedLength = formatted.length,
                separator = separator,
                decimalPlaces = decimalPlaces
            )
        )
    }

    /**
     * Converte dígitos raw em formato percentual.
     * Ex: "1234" → "12,34%"
     */
    private fun formatToPercentage(digits: String): String {
        if (digits.isBlank() || digits == "0") {
            return "0${separator}00%"
        }

        // Pad com zeros à esquerda para ter pelo menos decimalPlaces+1 dígitos
        val padded = digits.padStart(decimalPlaces + 1, '0')

        // Separa parte inteira e decimal
        val decimalStart = padded.length - decimalPlaces
        val integerPart = padded.substring(0, decimalStart)
        val decimalPart = padded.substring(decimalStart)

        return "$integerPart$separator$decimalPart%"
    }
}

/**
 * Mapeia posições do cursor entre texto original e formatado.
 */
class PercentageOffsetMapping(
    private val originalLength: Int,
    private val formattedLength: Int,
    private val separator: Char,
    private val decimalPlaces: Int
) : OffsetMapping {

    /**
     * Original → Transformado
     * Ex: "123|4" → "1,23|4%" (cursor após 3)
     */
    override fun originalToTransformed(offset: Int): Int {
        if (originalLength == 0) return 0

        val padded = "0".repeat(maxOf(0, decimalPlaces + 1 - originalLength))
        val totalDigits = padded.length + originalLength
        val decimalStart = totalDigits - decimalPlaces

        val adjustedOffset = offset + padded.length

        return when {
            adjustedOffset <= decimalStart -> adjustedOffset
            adjustedOffset <= totalDigits -> adjustedOffset + 1 // +1 pelo separator
            else -> formattedLength
        }
    }

    /**
     * Transformado → Original
     * Ex: "1,23|4%" → "123|4" (cursor volta para raw)
     */
    override fun transformedToOriginal(offset: Int): Int {
        if (formattedLength == 0) return 0

        val padded = "0".repeat(maxOf(0, decimalPlaces + 1 - originalLength))
        val totalDigits = padded.length + originalLength
        val decimalStart = totalDigits - decimalPlaces

        // Remove caracteres de formatação antes do offset
        val digitsBeforeOffset = when {
            offset <= decimalStart -> offset
            offset <= decimalStart + 1 -> decimalStart // no separator
            offset <= totalDigits + 1 -> offset - 1 // após separator
            else -> totalDigits
        }

        return maxOf(0, digitsBeforeOffset - padded.length)
    }
}