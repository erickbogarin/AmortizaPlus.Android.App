package com.elab.amortizaplus.domain.calculator

import com.elab.amortizaplus.domain.model.ExtraAmortization
import com.elab.amortizaplus.domain.model.ExtraAmortizationStrategy
import com.elab.amortizaplus.domain.util.MathUtils.roundTwo

/**
 * Representa uma amortização extra pronta para ser aplicada no cálculo.
 * Encapsula o valor monetário já arredondado e a estratégia (reduzir prazo ou parcela).
 */
data class ExtraAmortizationInput(
    val amount: Double,
    val reduceTerm: Boolean
) {

    val strategy: ExtraAmortizationStrategy =
        if (reduceTerm) ExtraAmortizationStrategy.REDUCE_TERM else ExtraAmortizationStrategy.REDUCE_PAYMENT

    companion object {
        fun from(extra: ExtraAmortization) = ExtraAmortizationInput(
            amount = extra.amount.roundTwo(),
            reduceTerm = extra.strategy == ExtraAmortizationStrategy.REDUCE_TERM
        )
    }
}
