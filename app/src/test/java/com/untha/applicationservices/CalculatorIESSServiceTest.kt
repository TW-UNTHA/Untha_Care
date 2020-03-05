package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.PERCENTAJE_APORTE_IESS_PRIVADO
import junit.framework.Assert.assertEquals
import junitparams.JUnitParamsRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.math.RoundingMode

@RunWith(JUnitParamsRunner::class)
class CalculatorIESSServiceTest {
    val calculatorIESSService = CalculatorIESSService()

    @Test
    fun `should return 37,80 of IESS contribution  when my salary is 400`() {
        val startDate = "2020-01-01"
        val endDate = "2020-01-15"
        val salary = 500.00.toBigDecimal()
        val salaryExpected = BigDecimal.valueOf(233.3333333338)
        val percentageIESSExpected =
            salaryExpected.multiply(PERCENTAJE_APORTE_IESS_PRIVADO.toBigDecimal())
                .setScale(2, RoundingMode.HALF_UP)


        val result =
            calculatorIESSService.getAportacionMensualIESS(startDate, endDate, salary)

        assertEquals(percentageIESSExpected, result)
    }



    @Test
    fun `should return 0 of fondo de reserva contribution when period is less than one year`() {
        val expectedValue = BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-01-01"
        val endDate = "2019-12-30"

        val result = calculatorIESSService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 37,49 of fondo de reserva contribution when period is one year`() {
        val expectedValue = BigDecimal(0).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-01-01"
        val endDate = "2020-01-01"

        val result = calculatorIESSService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 0,00 of fondo de reserva contribution when period is more than one year`() {
        val expectedValue = BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(400.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-03-01"

        val result = calculatorIESSService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 24,99 of fondo de reserva contribution when period is more than one year`() {
        val expectedValue = BigDecimal(24.99).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(300.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-03-02"

        val result = calculatorIESSService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }
}
