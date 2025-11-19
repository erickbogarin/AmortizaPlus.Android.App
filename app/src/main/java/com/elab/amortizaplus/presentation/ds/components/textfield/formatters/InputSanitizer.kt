package com.elab.amortizaplus.presentation.ds.components.textfield.formatters

import java.text.DecimalFormatSymbols
import java.util.Locale
/**
 * Funções auxiliares para sanitização de entrada.
 * Evita duplicação e centraliza lógica de filtragem.
 */
object InputSanitizers {
    /**
     * Aceita apenas dígitos (0-9).
     */
    fun digitsOnly(input: String): String =
        input.filter { it.isDigit() }
    /**
     * Aceita dígitos + um único separador decimal.
     *
     * ✅ ROBUSTEZ: Previne estados intermediários inválidos:
     * - "." isolado
     * - " ,"
     * - "12,,3"
     *
     * @param decimalSeparator separador decimal (ex: '.' ou ',')
     * @param allowMultiple se false, aceita apenas UM separador
     */
    fun digitsAndDecimal(
        input: String,
        decimalSeparator: Char = ',',
        allowMultiple: Boolean = false
    ): String {
// Remove espaços em branco
        val cleaned = input.filter { !it.isWhitespace() }
        if (allowMultiple) {
            return cleaned.filter { it.isDigit() || it == decimalSeparator }
        }
        var separatorFound = false
        var hasDigitsBeforeSeparator = false
        return buildString {
            cleaned.forEach { char ->
                when {
                    char.isDigit() -> {
                        append(char)
                        if (!separatorFound) {
                            hasDigitsBeforeSeparator = true
                        }
                    }
                    char == decimalSeparator && !separatorFound -> {
// ✅ VALIDAÇÃO: Só aceita separador se houver dígito antes
// Previne inputs como ",5" (força "0,5")
                        if (hasDigitsBeforeSeparator) {
                            separatorFound = true
                            append(char)
                        }
                    }
                }
            }
// ✅ LIMPEZA: Remove separador final isolado ("12," → "12")
            if (endsWith(decimalSeparator) && lastIndex == indexOf(decimalSeparator))
            {
                deleteCharAt(lastIndex)
            }
        }
    }
    /**
     * Aceita apenas letras (maiúsculas/minúsculas).
     */
    fun lettersOnly(input: String): String =
        input.filter { it.isLetter() }
    /**
     * Aceita letras, dígitos e espaços.
     */
    fun alphanumeric(input: String): String =
        input.filter { it.isLetterOrDigit() || it.isWhitespace() }
    /**
     * Limita o tamanho máximo da string.
     */
    fun maxLength(input: String, max: Int): String =
        input.take(max)
    /**
     * Remove todos os caracteres não-numéricos (útil para CPF, telefone, etc).
     */
    fun numbersOnly(input: String): String =
        input.filter { it.isDigit() }
}
/**
 * Extension functions para Locale (útil para formatters).
 */
object LocaleUtils {
    /**
     * Retorna o separador decimal do Locale.
     * Ex: Locale.US → '.' | Locale("pt", "BR") → ','
     */
    fun Locale.decimalSeparator(): Char =
        DecimalFormatSymbols.getInstance(this).decimalSeparator
    /**
     * Retorna o separador de milhares do Locale.
     * Ex: Locale.US → ',' | Locale("pt", "BR") → '.'
     */
    fun Locale.groupingSeparator(): Char =
        DecimalFormatSymbols.getInstance(this).groupingSeparator
}