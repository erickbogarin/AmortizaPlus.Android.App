package com.elab.amortizaplus.presentation.ds.foundation

import androidx.compose.ui.graphics.Color

/**
 * Tokens de cores do Design System.
 *
 * Cores semânticas adicionais que complementam o MaterialTheme.colorScheme.
 * Use estes tokens quando as cores do Material 3 não expressam claramente
 * a intenção (ex: sucesso, aviso, informação).
 */
object AppColors {

    // =========================================================================
    // SEMÂNTICAS (complementam Material 3)
    // =========================================================================

    /**
     * Âmbar de atenção (aviso não crítico).
     *
     * Uso:
     * - Alertas não críticos
     * - Avisos informativos
     * - Estados intermediários
     */
    val Warning = Color(0xFFFFB300)
    val WarningContainer = Color(0xFFFFE082)
    val OnWarning = Color(0xFF2B1F00)
    val OnWarningContainer = Color(0xFF3A2B00)

    // =========================================================================
    // LIGHT MODE (Material 3 base)
    // =========================================================================

    val Primary = Color(0xFF1976D2)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFD1E4FF)
    val OnPrimaryContainer = Color(0xFF001D35)

    val Secondary = Color(0xFF535E71)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFD7E3F8)
    val OnSecondaryContainer = Color(0xFF101C2B)

    // Tertiary = economia/ganhos (semântica do domínio)
    val Tertiary = Color(0xFF2E7D32)
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = Color(0xFFA5D6A7)
    val OnTertiaryContainer = Color(0xFF1B5E20)

    val Error = Color(0xFFB3261E)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFF9DEDC)
    val OnErrorContainer = Color(0xFF410E0B)

    val Background = Color(0xFFFDFCFF)
    val OnBackground = Color(0xFF1A1C1E)
    val Surface = Color(0xFFFDFCFF)
    val OnSurface = Color(0xFF1A1C1E)
    val SurfaceVariant = Color(0xFFE0E2EC)
    val OnSurfaceVariant = Color(0xFF44474E)

    val Outline = Color(0xFF74777F)
    val OutlineVariant = Color(0xFFC4C6D0)

    // =========================================================================
    // DARK MODE (Material 3 base)
    // =========================================================================

    val PrimaryDark = Color(0xFF9ECAFF)
    val OnPrimaryDark = Color(0xFF003258)
    val PrimaryContainerDark = Color(0xFF00497D)
    val OnPrimaryContainerDark = Color(0xFFD1E4FF)

    val SecondaryDark = Color(0xFFBBC7DB)
    val OnSecondaryDark = Color(0xFF253140)
    val SecondaryContainerDark = Color(0xFF3C4758)
    val OnSecondaryContainerDark = Color(0xFFD7E3F8)

    val TertiaryDark = Color(0xFF81C784)
    val OnTertiaryDark = Color(0xFF003300)
    val TertiaryContainerDark = Color(0xFF1B5E20)
    val OnTertiaryContainerDark = Color(0xFFA5D6A7)

    val ErrorDark = Color(0xFFF2B8B5)
    val OnErrorDark = Color(0xFF601410)
    val ErrorContainerDark = Color(0xFF8C1D18)
    val OnErrorContainerDark = Color(0xFFF9DEDC)

    val BackgroundDark = Color(0xFF1A1C1E)
    val OnBackgroundDark = Color(0xFFE2E2E6)
    val SurfaceDark = Color(0xFF1A1C1E)
    val OnSurfaceDark = Color(0xFFE2E2E6)
    val SurfaceVariantDark = Color(0xFF44474E)
    val OnSurfaceVariantDark = Color(0xFFC4C6D0)

    val OutlineDark = Color(0xFF8D9199)
    val OutlineVariantDark = Color(0xFF44474E)

    // =========================================================================
    // SEMÂNTICAS (Dark)
    // =========================================================================

    val WarningDark = Color(0xFFFFD54F)
    val WarningContainerDark = Color(0xFF5C3B00)
    val OnWarningDark = Color(0xFF2A1C00)
    val OnWarningContainerDark = Color(0xFFFFE082)
}
