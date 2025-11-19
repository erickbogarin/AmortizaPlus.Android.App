package com.elab.amortizaplus.presentation.ds.components.textfield.formatters

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
/**
 * Strategy para formatação de campos de texto.
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │ FLUXO DE DADOS (simplificado e unificado) │
 * ├─────────────────────────────────────────────────────────────┤
 * │ │
 * │ User Input → sanitize() → parse() → [ViewModel Raw Value] │
 * │ │
 * │ [ViewModel Raw Value] → formatForDisplay() → [TextField] │
 * │ ↓ │
 * │ VisualTransformation (cursor) │
 * │ │
 * └─────────────────────────────────────────────────────────────┘
 *
 * Responsabilidades CLARAMENTE separadas:
 *
 * 1. sanitize() - Filtra entrada inválida DURANTE digitação
 * 2. parse() - Converte string → raw value (para ViewModel)
 * 3. formatForDisplay() - Formata raw value para exibição visual
 * 4. createTransformation() - Gerencia APENAS cursor (opcional)
 */
interface InputFormatter {
    /**
     * [FASE 1: INPUT] Limpa entrada do usuário durante digitação.
     *
     * Chamado: onValueChange (tempo real)
     * Propósito: Aceitar apenas caracteres válidos
     *
     * Exemplo (Money): "R$ 123abc" → "123"
     * Exemplo (Percentage): "12.34.56%" → "12.34"
     * Exemplo (CPF): "123.456abc" → "123456"
     */
    fun sanitize(input: String): String
    /**
     * [FASE 2: PARSE] Converte string sanitizada → raw value.
     *
     * Chamado: onValueChange (após sanitize)
     * Propósito: Gerar valor "cru" para ViewModel
     *
     * Exemplo (Money): "12345" → "12345" (centavos)
     * Exemplo (Percentage): "12.34" → "1234" (basis points)
     * Exemplo (CPF): "12345678900" → "12345678900"
     *
     * ⚠️ IMPORTANTE: Retorna SEMPRE valor não-formatado.
     */
    fun parse(input: String): String = sanitize(input)
    /**
     * [FASE 3: DISPLAY] Formata raw value para exibição visual.
     *
     * Chamado: Ao receber valor do ViewModel
     * Propósito: Preparar texto para o TextField ANTES da transformation
     *
     * Exemplo (Money): "12345" → "R$ 123,45"
     * Exemplo (Percentage): "1234" → "12,34%"
     * Exemplo (CPF): "12345678900" → "123.456.789-00"
     *
     * ⚠️ CRITICAL: Este é o ÚNICO lugar que formata visualmente.
     * VisualTransformation NÃO deve duplicar esta lógica.
     */
    fun formatForDisplay(rawValue: String): String = rawValue
    /**
     * [FASE 4: CURSOR] Cria transformação para gerenciar cursor.
     *
     * Propósito: Mapear posições cursor entre texto cru ↔ formatado
     *
     * Default: Cursor no final (SimpleCursorTransformation)
     * Custom: Para máscaras intercaladas (CPF, telefone, CEP)
     *
     * ⚠️ NOTE: NÃO deve formatar texto aqui! Use formatForDisplay().
     */
    fun createTransformation(): VisualTransformation =
        SimpleCursorTransformation()
}
/**
 * Transformação padrão: gerencia APENAS cursor.
 * Não aplica formatação (isso é feito em formatForDisplay).
 */
class SimpleCursorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = text,
            offsetMapping = SimpleCursorMapping(text.text.length)
        )
    }
}
/**
 * Mapeamento simples: cursor sempre no final.
 * Ideal para formatações "sufixadas" (R$, %, etc).
 */
private class SimpleCursorMapping(
    private val length: Int
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int = length
    override fun transformedToOriginal(offset: Int): Int = length
}
/**
 * Transformação avançada: gerencia cursor + formatação intercalada.
 * Use APENAS para máscaras complexas (CPF, telefone, CEP).
 *
 * ⚠️ Para formatações simples, use formatForDisplay() + SimpleCursorTransformation.
 */
abstract class MaskTransformation : VisualTransformation {
    /**
     * Aplica máscara ao texto cru.
     * Ex: "12345678900" → "123.456.789-00"
     */
    protected abstract fun applyMask(raw: String): String
    /**
     * Cria mapeamento de cursor customizado.
     */
    protected abstract fun createOffsetMapping(raw: String, masked: String):
            OffsetMapping
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isBlank()) {
            return TransformedText(
                text = AnnotatedString(""),
                offsetMapping = OffsetMapping.Identity
            )
        }
        val masked = applyMask(raw)
        return TransformedText(
            text = AnnotatedString(masked),
            offsetMapping = createOffsetMapping(raw, masked)
        )
    }
}
/**
 * Helper para construir máscaras complexas de forma declarativa.
 * Reduz boilerplate em formatters com máscaras intercaladas.
 *
 * Uso:
 * ```kotlin
 * val masked = buildMask(raw) {
 * position(3, '.') // Adiciona '.' ANTES do caractere na posição 3
 * position(6, '.')
 * position(9, '-')
 * }
 * // "12345678900" → "123.456.789-00"
 * ```
 */
class MaskBuilder(private val raw: String) {
    private val maskPositions = mutableMapOf<Int, Char>()
    /**
     * Define posição onde um caractere de máscara deve ser inserido.
     *
     * @param index Índice no texto RAW onde a máscara será inserida ANTES
     * @param char Caractere da máscara (ex: '.', '-', '/', etc)
     */
    fun position(index: Int, char: Char) {
        maskPositions[index] = char
    }
    /**
     * Constrói o texto mascarado.
     */
    fun build(): String = buildString {
        raw.forEachIndexed { index, rawChar ->
// Adiciona máscara ANTES deste caractere, se houver
            maskPositions[index]?.let { maskChar ->
                append(maskChar)
            }
            append(rawChar)
        }
    }
}
/**
 * DSL function para criar máscaras.
 */
inline fun buildMask(raw: String, builder: MaskBuilder.() -> Unit): String {
    return MaskBuilder(raw).apply(builder).build()
}