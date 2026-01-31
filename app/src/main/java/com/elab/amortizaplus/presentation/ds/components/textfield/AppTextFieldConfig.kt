package com.elab.amortizaplus.presentation.ds.components.textfield

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.elab.amortizaplus.presentation.ds.components.textfield.formatters.*
import com.elab.amortizaplus.presentation.ds.components.textfield.transformation.MoneyVisualTransformation

data class TextFieldConfig(
    /**
     * Opções de teclado (tipo, ação IME, capitalização).
     */
    val keyboard: KeyboardOptions,
    /**
     * Formatter para lógica de entrada (sanitizar, parse, serialize).
     */
    val formatter: InputFormatter,
    /**
     * Transformação visual (máscara, formatação).
     * Gerada automaticamente pelo formatter.
     */
    val visualTransformation: VisualTransformation = formatter.createTransformation()
)
/**
 * Converte a variante em configuração completa.
 * Factory method para evitar duplicação.
 */
fun TextFieldVariant.toConfig(): TextFieldConfig = when (this) {
    TextFieldVariant.Default -> TextFieldConfig(
        keyboard = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            capitalization = KeyboardCapitalization.Sentences
        ),
        formatter = DefaultFormatter()
    )
    TextFieldVariant.Number -> TextFieldConfig(
        keyboard = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        formatter = NumberFormatter()
    )
    TextFieldVariant.Money -> run {
        val formatter = MoneyFormatter()
        TextFieldConfig(
            keyboard = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            formatter = formatter,
            visualTransformation = MoneyVisualTransformation(formatter)
        )
    }

    TextFieldVariant.Percentage -> run {
        val formatter = PercentageFormatter()
        TextFieldConfig(
            keyboard = KeyboardOptions(
                keyboardType = KeyboardType.Number,  // ✅ Number, não Decimal
                imeAction = ImeAction.Next
            ),
            formatter = formatter,
            visualTransformation = formatter.createTransformation()  // ✅ Usa a transformation
        )
    }
    TextFieldVariant.MonthYear -> run {
        val formatter = MonthYearFormatter()
        TextFieldConfig(
            keyboard = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            formatter = formatter,
            visualTransformation = formatter.createTransformation()
        )
    }
}
