package com.elab.amortizaplus.presentation.screens.simulation.validation

import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulationInputValidatorTest {

    private val validator = SimulationInputValidator()

    @Test
    fun `loan amount blank should return required message`() {
        val result = validator.validateLoanAmount("")

        assertEquals(SimulationTexts.requiredField, result.message)
    }

    @Test
    fun `loan amount greater than max should fail`() {
        val result = validator.validateLoanAmount("1000000001")

        assertEquals(SimulationTexts.loanAmountMax, result.message)
    }

    @Test
    fun `loan amount with positive cents should be valid`() {
        val result = validator.validateLoanAmount("12345")

        assertTrue(result.isValid)
        assertNull(result.message)
    }

    @Test
    fun `interest rate as basis points should validate max limit`() {
        val result = validator.validateInterestRate("3001")

        assertEquals(SimulationTexts.maxRate, result.message)
    }

    @Test
    fun `interest rate with valid basis points should be valid`() {
        val result = validator.validateInterestRate("1300")

        assertTrue(result.isValid)
    }

    @Test
    fun `terms above 600 should fail`() {
        val result = validator.validateTerms("601")

        assertEquals(SimulationTexts.maxTerms, result.message)
    }

    @Test
    fun `start date with invalid month should fail`() {
        val result = validator.validateStartDate("132026")

        assertEquals(SimulationTexts.invalidDate, result.message)
    }

    @Test
    fun `start date with valid month year should be valid`() {
        val result = validator.validateStartDate("022026")

        assertTrue(result.isValid)
    }

    @Test
    fun `extra month should require value`() {
        val result = validator.validateExtraMonth("", 420)

        assertEquals(SimulationTexts.extraMonthRequired, result.message)
    }

    @Test
    fun `extra month should respect term limit`() {
        val result = validator.validateExtraMonth("421", 420)

        assertEquals("${SimulationTexts.extraMonthTooHighPrefix} 420", result.message)
    }

    @Test
    fun `extra month inside range should be valid`() {
        val result = validator.validateExtraMonth("12", 420)

        assertTrue(result.isValid)
    }

    @Test
    fun `extra amount should require value greater than zero`() {
        val result = validator.validateExtraAmount("0")

        assertEquals(SimulationTexts.extraAmountTooLow, result.message)
    }

    @Test
    fun `extra amount with positive cents should be valid`() {
        val result = validator.validateExtraAmount("76000")

        assertTrue(result.isValid)
    }
}
