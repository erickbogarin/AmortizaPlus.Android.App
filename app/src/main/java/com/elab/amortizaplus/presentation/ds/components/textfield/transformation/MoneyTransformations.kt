package com.elab.amortizaplus.presentation.ds.components.textfield.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.elab.amortizaplus.presentation.ds.components.textfield.formatters.MoneyFormatter

class MoneyTransformation(
    private val formatter: MoneyFormatter
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val formatted = formatter.formatForDisplay(raw)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = formatted.length
            override fun transformedToOriginal(offset: Int): Int = raw.length
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}