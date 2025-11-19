package com.elab.amortizaplus.presentation.ds.components.textfield

/**
 * Variantes disponíveis para TextField.
 * Define o tipo de entrada esperada e formatação aplicada.
 */
enum class TextFieldVariant {
    /**
     * Campo de texto padrão (nome, email, descrição).
     * Sem formatação.
     */
    Default,
    /**
     * Campo numérico puro (prazo em meses, quantidade).
     * Apenas dígitos, sem formatação visual.
     */
    Number,
    /**
     * Campo monetário (valor financiado, prestação).
     * Formata como: R$ 1.234,56
     */
    Money,
    /**
     * Campo de porcentagem (taxa de juros).
     * Formata como: 12,5%
     */
    Percentage,
}