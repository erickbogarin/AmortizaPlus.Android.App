package com.elab.amortizaplus.presentation.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.elab.amortizaplus.presentation.ds.foundation.AppColors
// =============================================================================
// IMPORTS NECESSÁRIOS (adicionar ao topo do arquivo)
// =============================================================================
import androidx.compose.ui.unit.sp

/**
 * Tema principal do AmortizaPlus.
 *
 * Implementa Material Design 3 com extensões personalizadas para cores semânticas.
 * Suporta modo claro e escuro automaticamente.
 *
 * Uso:
 * ```kotlin
 * AmortizaPlusTheme {
 *     // Seu conteúdo aqui
 *     val warningColor = LocalAppColors.current.warning
 * }
 * ```
 */

// =============================================================================
// COLOR SCHEMES
// =============================================================================

/**
 * Esquema de cores para modo claro.
 *
 * Baseado nas diretrizes Material 3 com cores da marca AmortizaPlus.
 */
private val LightColorScheme = lightColorScheme(
    // Primary (azul - ações principais)
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryContainer,
    onPrimaryContainer = AppColors.OnPrimaryContainer,

    // Secondary (cinza azulado - ações secundárias)
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnSecondary,
    secondaryContainer = AppColors.SecondaryContainer,
    onSecondaryContainer = AppColors.OnSecondaryContainer,

    // Tertiary (verde - economia/ganhos)
    tertiary = AppColors.Tertiary,
    onTertiary = AppColors.OnTertiary,
    tertiaryContainer = AppColors.TertiaryContainer,
    onTertiaryContainer = AppColors.OnTertiaryContainer,

    // Error (vermelho - erros e alertas)
    error = AppColors.Error,
    onError = AppColors.OnError,
    errorContainer = AppColors.ErrorContainer,
    onErrorContainer = AppColors.OnErrorContainer,

    // Background & Surface
    background = AppColors.Background,
    onBackground = AppColors.OnBackground,
    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.OnSurfaceVariant,

    // Outline
    outline = AppColors.Outline,
    outlineVariant = AppColors.OutlineVariant
)

/**
 * Esquema de cores para modo escuro.
 *
 * Cores ajustadas para melhor contraste e conforto visual em ambientes escuros.
 */
private val DarkColorScheme = darkColorScheme(
    // Primary
    primary = AppColors.PrimaryDark,
    onPrimary = AppColors.OnPrimaryDark,
    primaryContainer = AppColors.PrimaryContainerDark,
    onPrimaryContainer = AppColors.OnPrimaryContainerDark,

    // Secondary
    secondary = AppColors.SecondaryDark,
    onSecondary = AppColors.OnSecondaryDark,
    secondaryContainer = AppColors.SecondaryContainerDark,
    onSecondaryContainer = AppColors.OnSecondaryContainerDark,

    // Tertiary
    tertiary = AppColors.TertiaryDark,
    onTertiary = AppColors.OnTertiaryDark,
    tertiaryContainer = AppColors.TertiaryContainerDark,
    onTertiaryContainer = AppColors.OnTertiaryContainerDark,

    // Error
    error = AppColors.ErrorDark,
    onError = AppColors.OnErrorDark,
    errorContainer = AppColors.ErrorContainerDark,
    onErrorContainer = AppColors.OnErrorContainerDark,

    // Background & Surface
    background = AppColors.BackgroundDark,
    onBackground = AppColors.OnBackgroundDark,
    surface = AppColors.SurfaceDark,
    onSurface = AppColors.OnSurfaceDark,
    surfaceVariant = AppColors.SurfaceVariantDark,
    onSurfaceVariant = AppColors.OnSurfaceVariantDark,

    // Outline
    outline = AppColors.OutlineDark,
    outlineVariant = AppColors.OutlineVariantDark
)

// =============================================================================
// EXTENDED COLORS (Cores Semânticas)
// =============================================================================

/**
 * Cores semânticas adicionais que não fazem parte do Material 3 padrão.
 *
 * Acessíveis via LocalAppColors.current dentro de um AmortizaPlusTheme.
 */
data class AppExtendedColors(
    val warning: androidx.compose.ui.graphics.Color,
    val onWarning: androidx.compose.ui.graphics.Color,
    val warningContainer: androidx.compose.ui.graphics.Color,
    val onWarningContainer: androidx.compose.ui.graphics.Color
)

/**
 * Cores estendidas para modo claro.
 */
private val LightExtendedColors = AppExtendedColors(
    warning = AppColors.Warning,
    onWarning = AppColors.OnWarning,
    warningContainer = AppColors.WarningContainer,
    onWarningContainer = AppColors.OnWarningContainer
)

/**
 * Cores estendidas para modo escuro.
 *
 * Mantém contraste adequado para modo escuro.
 */
private val DarkExtendedColors = AppExtendedColors(
    warning = AppColors.WarningDark,
    onWarning = AppColors.OnWarningDark,
    warningContainer = AppColors.WarningContainerDark,
    onWarningContainer = AppColors.OnWarningContainerDark
)

/**
 * CompositionLocal para acessar cores estendidas.
 *
 * Uso:
 * ```kotlin
 * val warningColor = LocalAppColors.current.warning
 * Icon(tint = LocalAppColors.current.warning)
 * ```
 */
val LocalAppColors = staticCompositionLocalOf { LightExtendedColors }

// =============================================================================
// TEMA PRINCIPAL
// =============================================================================

/**
 * Tema principal do aplicativo AmortizaPlus.
 *
 * Características:
 * - Suporte automático a modo claro/escuro
 * - Cores estendidas para Warning
 * - Tipografia personalizada
 * - Status bar colorida (Android)
 *
 * @param darkTheme Se true, usa modo escuro (padrão: segue sistema)
 * @param dynamicColor Se true, usa cores dinâmicas do Android 12+ (desabilitado por padrão)
 * @param content Conteúdo da aplicação
 */
@Composable
fun AmortizaPlusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = colorScheme.surface.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalAppColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

// =============================================================================
// SHAPES
// =============================================================================

/**
 * Formas (shapes) do Design System.
 *
 * Define arredondamento de componentes (botões, cards, dialogs).
 */
private val AppShapes = androidx.compose.material3.Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(
        com.elab.amortizaplus.presentation.ds.foundation.AppDimens.cornerRadiusSmall
    ),
    small = androidx.compose.foundation.shape.RoundedCornerShape(
        com.elab.amortizaplus.presentation.ds.foundation.AppDimens.cornerRadiusSmall
    ),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(
        com.elab.amortizaplus.presentation.ds.foundation.AppDimens.cornerRadiusMedium
    ),
    large = androidx.compose.foundation.shape.RoundedCornerShape(
        com.elab.amortizaplus.presentation.ds.foundation.AppDimens.cornerRadiusLarge
    ),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(
        com.elab.amortizaplus.presentation.ds.foundation.AppDimens.cornerRadiusLarge
    )
)

// =============================================================================
// TYPOGRAPHY
// =============================================================================

/**
 * Tipografia do Design System.
 *
 * Define estilos de texto para todo o app.
 */
private val AppTypography = androidx.compose.material3.Typography(
    // Display (títulos grandes, hero)
    displayLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // Headline (cabeçalhos de seção)
    headlineLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // Title (títulos de cards, dialogs)
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body (texto principal)
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Label (botões, chips, badges)
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// =============================================================================
// EXTENSION PROPERTIES
// =============================================================================

/**
 * Extension para acessar cores estendidas de forma conveniente.
 *
 * Uso:
 * ```kotlin
 * MaterialTheme.colorScheme.warning // via extension
 * LocalAppColors.current.warning    // via CompositionLocal
 * ```
 */
val androidx.compose.material3.ColorScheme.warning: androidx.compose.ui.graphics.Color
    @Composable
    get() = LocalAppColors.current.warning
val androidx.compose.material3.ColorScheme.onWarning: androidx.compose.ui.graphics.Color
    @Composable
    get() = LocalAppColors.current.onWarning

val androidx.compose.material3.ColorScheme.warningContainer: androidx.compose.ui.graphics.Color
    @Composable
    get() = LocalAppColors.current.warningContainer

val androidx.compose.material3.ColorScheme.onWarningContainer: androidx.compose.ui.graphics.Color
    @Composable
    get() = LocalAppColors.current.onWarningContainer

