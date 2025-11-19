package com.elab.amortizaplus.presentation.util
import java.text.NumberFormat
import java.util.*
/**
 * Extensions para formatação de valores financeiros.
 *
 * Centraliza lógica de formatação, evitando duplicação e garantindo
 * consistência em toda a aplicação.
 */
/**
 * Formata um Double como moeda brasileira (BRL).
 *
 * Exemplo:
 * ```kotlin
 * 150000.0.toCurrencyBR() // "R$ 150.000,00"
 * 1234.56.toCurrencyBR() // "R$ 1.234,56"
 * ```
 */
fun Double.toCurrencyBR(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return formatter.format(this)
}
/**
 * Formata um Double como percentual.
 *
 * @param decimals Número de casas decimais (padrão: 2)
 *
 * Exemplo:
 * ```kotlin
 * 0.13.toPercent() // "13,00%"
 * 0.1052.toPercent(4) // "10,5200%"
 * ```
 */
fun Double.toPercent(decimals: Int = 2): String {
    val formatter = NumberFormat.getNumberInstance(Locale("pt", "BR"))
    formatter.minimumFractionDigits = decimals
    formatter.maximumFractionDigits = decimals
    return "${formatter.format(this * 100)}%"
}
/**
 * Formata um número sem casas decimais.
 *
 * Exemplo:
 * ```kotlin
 * 150000.0.toFormattedNumber() // "150.000"
 * 1234.56.toFormattedNumber() // "1.235" (arredonda)
 * ```
 */
fun Double.toFormattedNumber(): String {
    val formatter = NumberFormat.getNumberInstance(Locale("pt", "BR"))
    formatter.minimumFractionDigits = 0
    formatter.maximumFractionDigits = 0
    return formatter.format(this)
}
/**
 * Converte taxa percentual (como digitado pelo usuário) para decimal.
 *
 * Exemplo:
 * ```kotlin
 * "13".percentToDecimal() // 0.13
 * "10.5".percentToDecimal() // 0.105
 * ```
 *
 * @return Double decimal ou null se inválido
 */
fun String.percentToDecimal(): Double? {
    val cleaned = this.replace(",", ".")
    return cleaned.toDoubleOrNull()?.div(100.0)
}
/**
 * Limpa string numérica para parsing.
 *
 * Remove tudo exceto dígitos, ponto e vírgula.
 *
 * Exemplo:
 * ```kotlin
 * "R$ 1.500,00".cleanNumeric() // "1500,00"
 * "13 %".cleanNumeric() // "13"
 * ```
 */
fun String.cleanNumeric(): String {
    return this.filter { it.isDigit() || it == '.' || it == ',' }
}
/**
 * Formata prazo em meses de forma legível.
 *
 * Exemplo:
 * ```kotlin
 * 12.formatTerms() // "12 meses (1 ano)"
 * 36.formatTerms() // "36 meses (3 anos)"
 * 420.formatTerms() // "420 meses (35 anos)"
 * ```
 */
fun Int.formatTerms(): String {
    val years = this / 12
    val months = this % 12
    return when {
        years == 0 -> "$this ${if (this == 1) "mês" else "meses"}"
        months == 0 -> "$this meses (${years} ${if (years == 1) "ano" else "anos"})"
        else -> "$this meses (${years} anos e $months ${
            if (months == 1) "mês" else
                "meses"
        })"
    }
}