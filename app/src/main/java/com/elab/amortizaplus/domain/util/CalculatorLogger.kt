package com.elab.amortizaplus.domain.util

/**
 * Logger condicional para cálculos.
 * Centraliza logs de debug e permite desabilitar facilmente em produção.
 */
object CalculationLogger {

    private const val ENABLE_LOGS = false // Toggle para desenvolvimento

    fun log(tag: String, message: String) {
        if (ENABLE_LOGS) {
            println("$tag → $message")
        }
    }

    fun logExtraAmortization(month: Int, amount: Double, remainingBalance: Double) {
        log(
            "SAC_EXTRA",
            "💰 Mês=$month | Valor=${"%.2f".format(amount)} | Saldo após=${"%.2f".format(remainingBalance)}"
        )
    }

    fun logReduction(
        extraRatio: Double,
        linearMonths: Int,
        acceleratedMonths: Int,
        newAmortization: Double,
        newTotalTerms: Int
    ) {
        log("SAC_REDUCTION", """
            - Ratio de amortização extra: ${"%.4f".format(extraRatio)}
            - Meses lineares (base): $linearMonths
            - Meses após aceleração: $acceleratedMonths
            - Nova amortização: R$ ${"%.2f".format(newAmortization)}
            - Novo prazo total: $newTotalTerms meses
        """.trimIndent())
    }

    fun logCompletion(installmentCount: Int) {
        log("SAC_COMPLETE", "✅ Simulação concluída com $installmentCount parcelas")
    }
}