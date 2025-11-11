package com.elab.amortizaplus.presentation.ds.foundation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.Alignment

/**
 *
 * Padrões de animação do Design System.
 * Centraliza durações, easings e offsets para garantir
 * consistência visual em todas as transições do app.
 *
 * Baseado nas diretrizes do Material Design 3:
 * - Curta: 100-200ms (microinterações)
 * - Média: 250-300ms (transições de estado)
 * - Longa: 350-500ms (navegação)
 */
object AppAnimationDefaults {
// =========================================================================
// DURAÇÕES
// =========================================================================
    /** Animação muito rápida (microinterações, hover) */
    const val DurationVeryShort = 100

    /** Animação curta (fade, scale pequeno) */
    const val DurationShort = 200

    /** Animação média (transições de estado) */
    const val DurationMedium = 300

    /** Animação longa (navegação, grandes mudanças) */
    const val DurationLong = 400
// =========================================================================
// EASINGS
// =========================================================================
    /** Easing padrão (entrada e saída suaves) */
    val EasingStandard: Easing = FastOutSlowInEasing

    /** Easing para entrada (aceleração) */
    val EasingEnter: Easing = LinearOutSlowInEasing

    /** Easing para saída (desaceleração) */
    val EasingExit: Easing = FastOutLinearInEasing

    /** Easing linear (progresso, loading) */
    val EasingLinear: Easing = LinearEasing
// =========================================================================
// ANIMATION SPECS
// =========================================================================
    /** Spec de transição rápida */
    fun <T> shortTween() = tween<T>(
        durationMillis = DurationShort,
        easing = EasingStandard
    )

    /** Spec de transição média */
    fun <T> mediumTween() = tween<T>(
        durationMillis = DurationMedium,
        easing = EasingStandard
    )

    /** Spec de transição longa */
    fun <T> longTween() = tween<T>(
        durationMillis = DurationLong,
        easing = EasingStandard
    )

    /** Spec para fade in */
    fun fadeInSpec() = tween<Float>(
        durationMillis = DurationShort,
        easing = EasingEnter
    )

    /** Spec para fade out */
    fun fadeOutSpec() = tween<Float>(
        durationMillis = DurationShort,
        easing = EasingExit
    )
// =========================================================================
// OFFSETS
// =========================================================================
    /** Offset vertical pequeno (microinterações) */
    const val OffsetSmall = 8

    /** Offset vertical médio (transições de estado) */
    const val OffsetMedium = 16

    /** Offset vertical grande (navegação) */
    const val OffsetLarge = 32
// =========================================================================
// TRANSIÇÕES PRONTAS
// =========================================================================
    /**
     *
     * Fade in padrão (rápido e suave).
     */
    fun fadeIn(): EnterTransition = fadeIn(animationSpec = fadeInSpec())

    /**
     *
     * Fade out padrão (rápido e suave).
     */
    fun fadeOut(): ExitTransition = fadeOut(animationSpec = fadeOutSpec())

    /**
     *
     *
     * Slide vertical para cima (entrada).
     * @param offset Deslocamento inicial em pixels (padrão: médio)
     */
    fun slideInVertically(offset: Int = OffsetMedium): EnterTransition =
        slideInVertically(
            animationSpec = shortTween(),
            initialOffsetY = { offset }
        )

    /**
     *
     *
     * Slide vertical para baixo (saída).
     * @param offset Deslocamento final em pixels (padrão: médio)
     */
    fun slideOutVertically(offset: Int = OffsetMedium): ExitTransition =
        slideOutVertically(
            animationSpec = shortTween(),
            targetOffsetY = { offset }
        )

    /**
     *
     * Expand vertical (entrada).
     */
    fun expandVertically(): EnterTransition =
        expandVertically(
            animationSpec = mediumTween(),
            expandFrom = Alignment.Top
        )

    /**
     * Shrink vertical (saída).
     */
    fun shrinkVertically(): ExitTransition =
        shrinkVertically(
            animationSpec = mediumTween(),
            shrinkTowards = Alignment.Top
        )

    /**
     *
     * Scale in (entrada com zoom).
     */
    fun scaleIn(): EnterTransition =
        scaleIn(
            animationSpec = shortTween(),
            initialScale = 0.8f
        )

    /**
     *
     * Scale out (saída com zoom).
     */
    fun scaleOut(): ExitTransition =
        scaleOut(
            animationSpec = shortTween(),
            targetScale = 0.8f
        )
// =========================================================================
// TRANSIÇÕES COMPOSTAS
// =========================================================================
    /**
     *
     *
     * Entrada padrão (fade + slide).
     * Usada em mensagens de erro, tooltips, etc.
     */
    fun defaultEnter(): EnterTransition = fadeIn() + slideInVertically()

    /**
     *
     *
     * Saída padrão (fade + slide).
     * Usada em mensagens de erro, tooltips, etc.
     */
    fun defaultExit(): ExitTransition = fadeOut() + slideOutVertically()

    /**
     *
     *
     * Entrada dramática (fade + scale).
     * Usada em dialogs, confirmações importantes.
     */
    fun dramaticEnter(): EnterTransition = fadeIn() + scaleIn()

    /**
     *
     *
     * Saída dramática (fade + scale).
     * Usada em dialogs, confirmações importantes.
     */
    fun dramaticExit(): ExitTransition = fadeOut() + scaleOut()
}

