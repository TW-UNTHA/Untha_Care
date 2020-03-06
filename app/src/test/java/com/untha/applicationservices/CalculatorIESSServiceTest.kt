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
        val salary = 500.00.toBigDecimal()
        val percentageIESSExpected =
            salary.multiply(PERCENTAJE_APORTE_IESS_PRIVADO.toBigDecimal())
                .setScale(2, RoundingMode.HALF_UP)


        val result =
            calculatorIESSService.getAportacionMensualIESS(salary)

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

    @Test
    fun `should return contribution IESS finiquito with 2019-03-01 and 2020-03-02`() {
        val expectedValue = BigDecimal(1.89).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(300.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-03-02"

        val result =
            calculatorIESSService.getAportacionMensualIESSFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return contribution IESS finiquito with 2019-03-01 and 2020-03-25`() {
        val expectedValue = BigDecimal(23.63).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(300.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-03-25"

        val result =
            calculatorIESSService.getAportacionMensualIESSFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return contribution IESS finiquito with 2019-02-01 and 2020-02-29`() {
        val expectedValue = BigDecimal(28.35).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(300.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-02-01"
        val endDate = "2020-02-29"

        val result =
            calculatorIESSService.getAportacionMensualIESSFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }


    @Test
    fun `should return contribution IESS finiquito with 2020-02-04 and 2020-02-21`() {
        val expectedValue = BigDecimal(17.01).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(300.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2020-02-04"
        val endDate = "2020-02-21"

        val result =
            calculatorIESSService.getAportacionMensualIESSFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 0 of fondo de reserva contribution finiquito when period is less than one year`() {
        val expectedValue = BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-01-01"
        val endDate = "2019-12-30"

        val result = calculatorIESSService.getFondoReservaMensualizadoFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }
    @Test
    fun `should fondo de reserva contribution finiquito when period more than one year start date 2018-11-01 end date 2019-12-30`() {
        val expectedValue = BigDecimal(37.49).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2018-11-01"
        val endDate = "2019-12-30"

        val result = calculatorIESSService.getFondoReservaMensualizadoFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should fondo de reserva contribution finiquito when period more than one year start date 2019-05-14 end date 2019-05-28`() {
        val expectedValue = BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-05-14"
        val endDate = "2019-05-28"

        val result = calculatorIESSService.getFondoReservaMensualizadoFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }
}
