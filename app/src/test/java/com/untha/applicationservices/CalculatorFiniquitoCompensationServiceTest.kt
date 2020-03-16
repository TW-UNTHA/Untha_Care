package com.untha.applicationservices

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.RoundingMode

class CalculatorFiniquitoCompensationServiceTest{
    val calculatorFiniquitoCompensationService = CalculatorFiniquitoCompensationService()

    @Test
    fun `should return 12 salaries when hint is pregnancy`(){
        val salary = 400.toBigDecimal().setScale(2,RoundingMode.HALF_UP)
        val hint = "R3P1R3"
        val result = 4800.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        assertEquals(result, calculatorFiniquitoCompensationService.getCompensation(hint, salary) )
    }
    @Test
    fun `should return 18 salaries when hint is disability`(){
        val salary = 400.toBigDecimal().setScale(2,RoundingMode.HALF_UP)
        val hint = "R3P1R1"
        val result = 7200.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        assertEquals(result, calculatorFiniquitoCompensationService.getCompensation(hint, salary) )
    }

    @Test
    fun `should return 18 salaries when hint is substitute of disability`(){
        val salary = 400.toBigDecimal().setScale(2,RoundingMode.HALF_UP)
        val hint = "R3P1R2"
        val result = 7200.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        assertEquals(result, calculatorFiniquitoCompensationService.getCompensation(hint, salary) )
    }


    @Test
    fun `should return 0 salaries when hint is none `(){
        val salary = 400.toBigDecimal().setScale(2,RoundingMode.HALF_UP)
        val hint = "R3P1R4"
        val result = 0.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        assertEquals(result, calculatorFiniquitoCompensationService.getCompensation(hint, salary) )
    }

}