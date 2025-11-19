package com.elab.amortizaplus.presentation.ds.components.textfield.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.elab.amortizaplus.presentation.ds.components.textfield.formatters.MoneyFormatter

/**
 * Exibe o valor formatado (R$ 1.234,56) sem alterar o RAW.
 * O TextField continua editando apenas dígitos.
 */
class MoneyVisualTransformation(
    private val formatter: MoneyFormatter
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text                 // "12345"
        val formatted = formatter.formatForDisplay(raw) // "R$ 123,45"

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                formatted.length

            override fun transformedToOriginal(offset: Int): Int =
                raw.length
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
