package com.elab.amortizaplus.domain.model

data class Installment(
    val month: Int,
    val amortization: Double,
    val interest: Double,
    val installment: Double,
    val remainingBalance: Double,
    val extraAmortization: Double = 0.0
)