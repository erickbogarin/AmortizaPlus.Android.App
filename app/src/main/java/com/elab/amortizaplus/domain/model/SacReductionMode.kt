package com.elab.amortizaplus.domain.model

/**
 * Define o comportamento da redução de prazo após amortização extraordinária.
 */
enum class SacReductionMode {
    /** Cálculo exato (matemática pura SAC) — sem fator empírico */
    EXACT,

    /** Cálculo realista aproximando comportamento bancário (fator ~4.9) */
    REALISTIC
}