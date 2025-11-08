package com.elab.amortizaplus.domain.util

import kotlin.math.roundToInt

/**
 * Utilitários matemáticos para cálculos financeiros.
 * Centraliza lógica de arredondamento para garantir consistência.
 */
object MathUtils {

    /**
     * Arredonda um Double para 2 casas decimais.
     * Usado em valores monetários para evitar problemas de precisão.
     */
    fun Double.roundToTwoDecimals(): Double =
        (this * 100).roundToInt() / 100.0

    /**
     * Verifica se um valor é efetivamente zero (considerando precisão de ponto flutuante)
     */
    fun Double.isEffectivelyZero(epsilon: Double = 0.01): Boolean =
        this < epsilon && this > -epsilon

    /**
     * Garante que um valor não seja negativo (útil para saldos)
     */
    fun Double.nonNegative(): Double = maxOf(0.0, this)
}