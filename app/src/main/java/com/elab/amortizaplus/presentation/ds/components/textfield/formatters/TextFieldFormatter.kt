package com.elab.amortizaplus.presentation.ds.components.textfield.formatters

interface TextFieldFormatter {
    fun filter(input: String): String = input
    fun format(input: String): String = input
    fun unformat(input: String): String = input
}
