package com.elab.amortizaplus.domain.calculator

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlin.math.pow

class SacCalculatorMultipleAmortizationsTest {

    @Test
    fun `SAC com amortizacoes nos meses 8 e 16 deve quitar em cerca de 37 meses`() {
        val calc = SacCalculator()
        val rate = (1 + 0.13).pow(1.0 / 12.0) - 1

        val result = calc.calculate(
            loanAmount = 121_000.0,
            monthlyRate = rate,
            terms = 420,
            extraAmortizations = mapOf(
                8 to 76_000.0,
                16 to 10_000.0
            ),
            reduceTerm = true
        )

        // ✅ 1. Aplicação da amortização extra
        val month8 = result.first { it.month == 8 }
        assertEquals(76_000.0, month8.extraAmortization, 0.01)

        val month16 = result.first { it.month == 16 }
        assertEquals(10_000.0, month16.extraAmortization, 0.01)

        // ✅ 2. Parcela decrescente (característica SAC)
        assert(result.first().installment > result.last().installment) {
            "Parcelas devem ser decrescentes no SAC"
        }

        // ✅ 3. Prazo dentro do esperado
        assert(result.size in 36..38) {
            "Prazo esperado entre 36 e 38 meses, obtido: ${result.size}"
        }

        // ✅ 4. Saldo final quitado
        assertEquals(0.0, result.last().remainingBalance, 0.01)
    }

    @Test
    fun `SAC com amortizacoes e reducao de parcela deve manter 420 meses`() {
        val calc = SacCalculator()
        val rate = (1 + 0.13).pow(1.0 / 12.0) - 1

        val result = calc.calculate(
            loanAmount = 121_000.0,
            monthlyRate = rate,
            terms = 420,
            extraAmortizations = mapOf(
                8 to 76_000.0,
                16 to 10_000.0
            ),
            reduceTerm = false
        )

        // ✅ 1. O número de parcelas deve continuar o mesmo (não reduz prazo)
        assertEquals(420, result.size)

        // ✅ 2. As amortizações extras devem ser aplicadas corretamente
        val month8 = result.first { it.month == 8 }
        assertEquals(76_000.0, month8.extraAmortization, 0.01)

        val month16 = result.first { it.month == 16 }
        assertEquals(10_000.0, month16.extraAmortization, 0.01)

        // ✅ 3. O valor das parcelas deve cair após a primeira amortização
        val avgBefore = result.subList(1, 7).map { it.installment }.average()
        val avgAfter = result.subList(9, 15).map { it.installment }.average()
        assertTrue(
            "O valor médio das parcelas após a amortização deve ser menor (antes=$avgBefore, depois=$avgAfter)",
            avgAfter < avgBefore,
        )

        // ✅ 4. O saldo final deve chegar próximo de zero
        assertEquals(0.0, result.last().remainingBalance, 0.01)
    }

    @Test
    fun `SAC com pequenas amortizacoes nao deve reduzir prazo de forma desproporcional`() {
        val calc = SacCalculator()
        val rate = (1 + 0.13).pow(1.0 / 12.0) - 1

        val result = calc.calculate(
            loanAmount = 121_000.0,
            monthlyRate = rate,
            terms = 420,
            extraAmortizations = mapOf(
                12 to 1_000.0,  // amortização leve no mês 12
                24 to 500.0,    // amortização ainda menor
                36 to 1_500.0   // outra pequena amortização
            ),
            reduceTerm = true
        )

        val prazoObtido = result.size
        val reducao = 420 - prazoObtido

        println("🧮 Prazo obtido: $prazoObtido meses (redução de $reducao meses)")

        // ✅ 1. Deve continuar próximo de 420 meses (sem redução exagerada)
        assertTrue(
            "Pequenas amortizações não devem reduzir o prazo em mais de 10 meses (foi $reducao)",
            reducao in 0..10
        )

        // ✅ 2. Deve quitar corretamente no final
        assertEquals(0.0, result.last().remainingBalance, 0.01)
    }
}
