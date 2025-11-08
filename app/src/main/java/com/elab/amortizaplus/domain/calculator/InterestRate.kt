package com.elab.amortizaplus.domain.model

import kotlin.math.pow

/**
 * Representa uma taxa de juros que pode ser anual ou mensal.
 * Evita que a camada de UI precise saber como converter entre elas.
 */
sealed class InterestRate(val value: Double) {

    /** Taxa anual, ex: 0.13 (13% a.a.) */
    class Annual(rate: Double) : InterestRate(rate)

    /** Taxa mensal, ex: 0.0102 (1,02% a.m.) */
    class Monthly(rate: Double) : InterestRate(rate)

    /** Converte qualquer taxa para a equivalente mensal */
    fun asMonthly(): Double = when (this) {
        is Annual -> (1 + value).pow(1.0 / 12.0) - 1
        is Monthly -> value
    }

    /** Converte qualquer taxa para a equivalente anual */
    fun asAnnual(): Double = when (this) {
        is Annual -> value
        is Monthly -> (1 + value).pow(12.0) - 1
    }

    override fun toString(): String {
        return when (this) {
            is Annual -> "Anual (${String.format("%.2f", value * 100)}%)"
            is Monthly -> "Mensal (${String.format("%.2f", value * 100)}%)"
        }
    }
}
