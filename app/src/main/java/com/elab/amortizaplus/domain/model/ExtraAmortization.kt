package com.elab.amortizaplus.domain.model

data class ExtraAmortization(
    val month: Int,
    val amount: Double,
    val strategy: ExtraAmortizationStrategy
)