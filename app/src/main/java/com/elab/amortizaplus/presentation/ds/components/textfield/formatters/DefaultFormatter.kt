package com.elab.amortizaplus.presentation.ds.components.textfield.formatters

import androidx.compose.ui.text.input.VisualTransformation

// ========================================
// 1. DEFAULT FORMATTER
// ========================================

/**
 * Formatter padrão: sem formatação.
 * Para campos de texto livre (nome, email, endereço).
 */
class DefaultFormatter : InputFormatter {
    override fun sanitize(input: String): String = input

    override fun createTransformation(): VisualTransformation =
        VisualTransformation.None
}