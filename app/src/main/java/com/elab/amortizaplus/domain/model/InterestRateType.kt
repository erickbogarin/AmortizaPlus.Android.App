package com.elab.amortizaplus.domain.model

/**
 * Indica se a taxa informada pelo usuário está em base anual ou mensal.
 * Evita assumir anual por padrão na camada de domínio.
 */
enum class InterestRateType {
    ANNUAL,
    MONTHLY
}
