package com.elab.amortizaplus.domain.model

data class Payment(
    val month: Int,
    val principal: Double,
    val interest: Double,
    val total: Double,
    val balance: Double
)