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
class PercentageCursorTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {

        val transformedText = text

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                if (transformedText.isEmpty()) return 0

                val last = transformedText.length - 1

                // Se termina com %, cursor só pode ir até antes dele
                val maxCursor = if (transformedText.text.last() == '%')
                    last - 1
                else
                    last

                // Proteção total
                val safeMax = maxCursor.coerceAtLeast(0)

                return offset.coerceIn(0, safeMax)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (transformedText.isEmpty()) return 0

                val last = transformedText.length - 1
                val maxCursor = if (transformedText.text.last() == '%')
                    last - 1
                else
                    last

                val safeMax = maxCursor.coerceAtLeast(0)

                return offset.coerceIn(0, safeMax)
            }
        }

        return TransformedText(transformedText, offsetMapping)
    }
}
