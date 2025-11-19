package com.elab.amortizaplus.presentation.ds.components.textfield.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Transformação para porcentagem: gerencia APENAS cursor.
 *
 * ✅ IMPORTANTE: NÃO formata texto (isso é feito em formatForDisplay).
 * Apenas impede que cursor vá para depois do "%".
 */
private class PercentageCursorTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        // Não altera o texto - ele já vem formatado de formatForDisplay()
        return TransformedText(
            text = text,
            offsetMapping = PercentageOffsetMapping(text.text.length)
        )
    }
}

/**
 * Mapeamento de cursor para porcentagem.
 * Impede cursor depois do "%" (posição não editável).
 */
private class PercentageOffsetMapping(
    private val textLength: Int
) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        // Se texto termina com "%", cursor fica antes dele
        val maxCursorPos = if (textLength > 0) textLength - 1 else 0
        return offset.coerceIn(0, maxCursorPos)
    }

    override fun transformedToOriginal(offset: Int): Int {
        val maxCursorPos = if (textLength > 0) textLength - 1 else 0
        return offset.coerceIn(0, maxCursorPos)
    }
}