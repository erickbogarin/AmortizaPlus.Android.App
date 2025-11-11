package com.elab.amortizaplus.presentation.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elab.amortizaplus.presentation.designsystem.theme.AmortizaPlusTheme
import com.elab.amortizaplus.presentation.designsystem.theme.LocalAppColors
import com.elab.amortizaplus.presentation.ds.foundation.AppSpacing
import com.elab.amortizaplus.presentation.designsystem.theme.success
import com.elab.amortizaplus.presentation.designsystem.theme.warning
import com.elab.amortizaplus.presentation.designsystem.theme.info

/**
 * Preview completo do AmortizaPlusTheme.
 *
 * Exibe:
 * - Paleta de cores (Material 3 + Estendidas)
 * - Tipografia completa
 * - Componentes com tema aplicado
 * - Modo claro e escuro
 */
@Composable
fun ThemeShowcase(darkMode: Boolean = false) {
    AmortizaPlusTheme (darkTheme = darkMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(AppSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.large)
            ) {
                // Título
                Text(
                    text = "🎨 AmortizaPlus Theme Showcase",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = if (darkMode) "Dark Mode" else "Light Mode",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Divider()

                // Seções
                ColorPaletteSection()
                TypographySection()
                ComponentsSection()
                SemanticColorsSection()
            }
        }
    }
}

// =============================================================================
// PALETA DE CORES
// =============================================================================

@Composable
private fun ColorPaletteSection() {
    SectionTitle("Color Palette")

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
        // Primary
        ColorRow("Primary", MaterialTheme.colorScheme.primary)
        ColorRow("On Primary", MaterialTheme.colorScheme.onPrimary)
        ColorRow("Primary Container", MaterialTheme.colorScheme.primaryContainer)
        ColorRow("On Primary Container", MaterialTheme.colorScheme.onPrimaryContainer)

        Spacer(Modifier.height(AppSpacing.extraSmall))

        // Secondary
        ColorRow("Secondary", MaterialTheme.colorScheme.secondary)
        ColorRow("Secondary Container", MaterialTheme.colorScheme.secondaryContainer)

        Spacer(Modifier.height(AppSpacing.extraSmall))

        // Tertiary
        ColorRow("Tertiary", MaterialTheme.colorScheme.tertiary)
        ColorRow("Tertiary Container", MaterialTheme.colorScheme.tertiaryContainer)

        Spacer(Modifier.height(AppSpacing.extraSmall))

        // Error
        ColorRow("Error", MaterialTheme.colorScheme.error)
        ColorRow("Error Container", MaterialTheme.colorScheme.errorContainer)

        Spacer(Modifier.height(AppSpacing.extraSmall))

        // Surface
        ColorRow("Surface", MaterialTheme.colorScheme.surface)
        ColorRow("Background", MaterialTheme.colorScheme.background)
    }
}

@Composable
private fun ColorRow(name: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color, MaterialTheme.shapes.small)
        )
    }
}

// =============================================================================
// TIPOGRAFIA
// =============================================================================

@Composable
private fun TypographySection() {
    SectionTitle("Typography")

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
        Text("Display Large", style = MaterialTheme.typography.displayLarge)
        Text("Display Medium", style = MaterialTheme.typography.displayMedium)

        Spacer(Modifier.height(AppSpacing.small))

        Text("Headline Large", style = MaterialTheme.typography.headlineLarge)
        Text("Headline Medium", style = MaterialTheme.typography.headlineMedium)
        Text("Headline Small", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(AppSpacing.small))

        Text("Title Large", style = MaterialTheme.typography.titleLarge)
        Text("Title Medium", style = MaterialTheme.typography.titleMedium)
        Text("Title Small", style = MaterialTheme.typography.titleSmall)

        Spacer(Modifier.height(AppSpacing.small))

        Text("Body Large - Texto principal de leitura", style = MaterialTheme.typography.bodyLarge)
        Text("Body Medium - Texto secundário", style = MaterialTheme.typography.bodyMedium)
        Text("Body Small - Legendas", style = MaterialTheme.typography.bodySmall)

        Spacer(Modifier.height(AppSpacing.small))

        Text("Label Large", style = MaterialTheme.typography.labelLarge)
        Text("Label Medium", style = MaterialTheme.typography.labelMedium)
        Text("Label Small", style = MaterialTheme.typography.labelSmall)
    }
}

// =============================================================================
// COMPONENTES
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComponentsSection() {
    SectionTitle("Components")

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
        // Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            Button(onClick = {}) {
                Text("Primary")
            }
            OutlinedButton(onClick = {}) {
                Text("Secondary")
            }
            TextButton(onClick = {}) {
                Text("Text")
            }
        }

        // Cards
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(AppSpacing.medium)) {
                Text(
                    "Card Title",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Card content with body text style",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // TextField
        var text by remember { mutableStateOf("") }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Label") },
            modifier = Modifier.fillMaxWidth()
        )

        // Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text("Selected") }
            )
            FilterChip(
                selected = false,
                onClick = {},
                label = { Text("Unselected") }
            )
        }
    }
}

// =============================================================================
// CORES SEMÂNTICAS
// =============================================================================

@Composable
private fun SemanticColorsSection() {
    SectionTitle("Semantic Colors (Extended)")

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
        // Success
        Card(
            colors = CardDefaults.cardColors(
                containerColor = LocalAppColors.current.successContainer,
                contentColor = LocalAppColors.current.onSuccessContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(AppSpacing.medium),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.success
                )
                Column {
                    Text(
                        "Success Color",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Usado para validação, economia, ganhos",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Warning
        Card(
            colors = CardDefaults.cardColors(
                containerColor = LocalAppColors.current.warningContainer,
                contentColor = LocalAppColors.current.onWarningContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(AppSpacing.medium),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = LocalAppColors.current.warning
                )
                Column {
                    Text(
                        "Warning Color",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Usado para avisos não críticos",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Info
        Card(
            colors = CardDefaults.cardColors(
                containerColor = LocalAppColors.current.infoContainer,
                contentColor = LocalAppColors.current.onInfoContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(AppSpacing.medium),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = LocalAppColors.current.info
                )
                Column {
                    Text(
                        "Info Color",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Usado para dicas e informações contextuais",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// =============================================================================
// HELPERS
// =============================================================================

@Composable
private fun SectionTitle(title: String) {
    Column {
        Spacer(Modifier.height(AppSpacing.small))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(AppSpacing.medium))
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Light Mode", showBackground = true, heightDp = 3000)
@Composable
private fun PreviewThemeShowcaseLight() {
    ThemeShowcase(darkMode = false)
}

@Preview(name = "Dark Mode", showBackground = true, heightDp = 3000)
@Composable
private fun PreviewThemeShowcaseDark() {
    ThemeShowcase(darkMode = true)
}