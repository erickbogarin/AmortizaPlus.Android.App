package com.elab.amortizaplus.domain.model

/**
 * Estratégia aplicada a uma amortização extra:
 * - REDUCE_TERM: mantém valor da parcela e reduz o prazo total.
 * - REDUCE_PAYMENT: mantém prazo e reduz valor da parcela.
 */
enum class ExtraAmortizationStrategy {
    REDUCE_TERM,
    REDUCE_PAYMENT
}