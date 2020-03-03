package com.untha.applicationservices

import com.untha.automation.EntriesAnnualLeaveTest
import junit.framework.Assert.assertEquals
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Method
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@RunWith(JUnitParamsRunner::class)
class CalculatorsServiceTest {
    val calculatorsService = CalculatorsService()
    lateinit var stringToCalendar: Method
    lateinit var calculateDaysBetween: Method

    companion object {
        const val SIERRA_ORIENTE = 2
        const val COSTA_GALAPAGOS = 1
        const val COMPLETA = 1
        const val PARCIAL = 2
    }

    @Before
    fun init() {
        stringToCalendar =
            calculatorsService.javaClass.getDeclaredMethod("stringToCalendar", String::class.java)
        stringToCalendar.isAccessible = true

        calculateDaysBetween = calculatorsService.javaClass.getDeclaredMethod(
            "calculateDaysBetween",
            Calendar::class.java,
            Calendar::class.java
        )
        calculateDaysBetween.isAccessible = true
    }

    @Test
    fun `should transform String to Calendar`() {
        val stringDate = "2012-02-23"
        val calendarDate = stringToCalendar.invoke(calculatorsService, stringDate)
        assertThat(calendarDate, instanceOf(Calendar::class.java))
    }
    // <editor-fold desc="CALCULATE NUMBER OF DAYS">

    @Test
    fun `should return 360  when start date 01 January 2020 and end date 01 January 2021`() {
        val calculatorsService = CalculatorsService()
        val startDate = "2020-01-01"
        val endDate = "2021-01-01"
        val expectedValue = 360

        val calendarStartDate = stringToCalendar.invoke(calculatorsService, startDate)
        val calendarEndDate = stringToCalendar.invoke(calculatorsService, endDate)

        val numberOfDays =
            calculateDaysBetween.invoke(calculatorsService, calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 44 when start date 1 December 2019 and end date 15 January 2020 `() {
        val calculatorsService = CalculatorsService()
        val startDate = "2019-12-01"
        val endDate = "2020-01-15"
        val expectedValue = 44

        val calendarStartDate = stringToCalendar.invoke(calculatorsService, startDate)
        val calendarEndDate = stringToCalendar.invoke(calculatorsService, endDate)

        val numberOfDays =
            calculateDaysBetween.invoke(calculatorsService, calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 163 when start date 1 December 2019 and end date 14 May 2020 `() {
        val startDate = "2019-12-01"
        val endDate = "2020-05-14"
        val expectedValue = 163

        val calendarStartDate = stringToCalendar.invoke(calculatorsService, startDate)
        val calendarEndDate = stringToCalendar.invoke(calculatorsService, endDate)

        val numberOfDays =
            calculateDaysBetween.invoke(calculatorsService, calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }


    @Test
    fun `should return 329 when start date 1 January 2020 and end date 30 November 2020 `() {
        val startDate = "2020-01-01"
        val endDate = "2020-11-30"
        val expectedValue = 329

        val calendarStartDate = stringToCalendar.invoke(calculatorsService, startDate)
        val calendarEndDate = stringToCalendar.invoke(calculatorsService, endDate)

        val numberOfDays =
            calculateDaysBetween.invoke(calculatorsService, calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 160 when start date 21 February 2020 and end date 31 July 2020 `() {
        val startDate = "2020-02-21"
        val endDate = "2020-07-31"
        val expectedValue = 160

        val calendarStartDate = stringToCalendar.invoke(calculatorsService, startDate)
        val calendarEndDate = stringToCalendar.invoke(calculatorsService, endDate)

        val numberOfDays =
            calculateDaysBetween.invoke(calculatorsService, calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 133 when start date 16 October 2019 and end date 29 February 2020 `() {
        val startDate = "2019-10-16"
        val endDate = "2020-02-29"
        val expectedValue = 133

        val calendarStartDate = stringToCalendar.invoke(calculatorsService, startDate)
        val calendarEndDate = stringToCalendar.invoke(calculatorsService, endDate)

        val numberOfDays =
            calculateDaysBetween.invoke(calculatorsService, calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 132 when start date 16 October 2020 and end date 28 February 2021 `() {
        val startDate = "2020-10-16"
        val endDate = "2021-02-28"
        val expectedValue = 132

        val calendarStartDate = stringToCalendar.invoke(calculatorsService, startDate)
        val calendarEndDate = stringToCalendar.invoke(calculatorsService, endDate)

        val numberOfDays =
            calculateDaysBetween.invoke(calculatorsService, calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    //</editor-fold>
    // <editor-fold desc="DECIMO TERCERO MENSUALIZADO">
    @Test
    fun `should return decimo tercero mensualizado = 33,33 when receive a salary of 400`() {
        val salary = 400
        val expectedValue = BigDecimal(33.33).setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoTercerSueldoMensualizado(salary.toBigDecimal())
        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 37,5 decimo tercero mensualizado when receive a salary of 450`() {
        val salary = 450
        val expectedValue = BigDecimal(37.5).setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoTercerSueldoMensualizado(salary.toBigDecimal())

        assertEquals(expectedValue, result)
    }

    //</editor-fold>
    // <editor-fold desc="DECIMO TERCERO ACUMULADO">
    @Test
    fun `should return 500 when start date is 1 December and end date is 30 of November and salary is 500`() {
        val salary = 500
        val expectedValue = BigDecimal(500).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-12-01"
        val endDate = "2020-11-30"

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            startDate,
            endDate
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 48,89 when start date is 1st December 2019 and end date is 15 January 2020 and salary is 400`() {
        val salary = 400
        val expectedValue = BigDecimal(48.89).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-12-01"
        val endDate = "2020-01-15"

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            startDate,
            endDate
        )

        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return 181,11 when start date is 1st December 2019 and end date is 14 May 2020 and salary is 400`() {
        val salary = 400
        val expectedValue = BigDecimal(181.11).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-12-01"
        val endDate = "2020-05-14"

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            startDate,
            endDate
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 400 when start date is 1st December 2019 and end date is 30 November 2022 and salary is 400`() {
        val salary = 400
        val expectedValue = BigDecimal(400).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-12-01"
        val endDate = "2022-11-30"

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            startDate,
            endDate
        )

        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return 365,56 when start date is 1 January 2020 and end date is 30 November 2020 and salary is 400`() {
        val salary = 400
        val expectedValue = BigDecimal(365.56).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2020-01-01"
        val endDate = "2020-11-30"

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            startDate,
            endDate
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 122,22 when start date is 21 March 2020 and end date is 11 July 2020 and salary is 400`() {
        val salary = 400
        val expectedValue = BigDecimal(122.22).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2020-03-21"
        val endDate = "2020-07-11"

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            startDate,
            endDate
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 145,56 when start date is 31 July 2018 and end date is 12 de  April 2020 and salary is 400`() {
        val salary = 400
        val expectedValue = BigDecimal(145.56).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2018-07-31"
        val endDate = "2020-04-12"

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            startDate,
            endDate
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 32,22 when start date is 15 Ag 2019 and end date is 30 de December 2020 and salary is 400`() {
        val salary = 400
        val expectedValue = BigDecimal(32.22).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-08-15"
        val endDate = "2020-12-30"

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            startDate,
            endDate
        )

        assertEquals(expectedValue, result)
    }

    //</editor-fold>
    // <editor-fold desc="DECIMO CUARTO MENSUALIZADO">
    @Test
    fun `should return decimo cuarto mensualizado 33,33 when SBU is 400`() {
        val expectedValue = BigDecimal(33.33).setScale(2, RoundingMode.HALF_UP)
        val numberOfHours = 30

        val result = calculatorsService.getDecimoCuartoSueldoMensualizado(COMPLETA)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return decimo cuarto mensualizado 29,17 when SBU is 400`() {
        val expectedValue = BigDecimal(29.17).setScale(2, RoundingMode.HALF_UP)
        val numberOfHours = 35

        val result = calculatorsService.getDecimoCuartoSueldoMensualizado(PARCIAL, numberOfHours)

        assertEquals(expectedValue, result)
    }

    //</editor-fold>
    // <editor-fold desc="DECIMO CUARTO ACUMULADO">
    @Test
    fun `should return SBU 400 when area is Sierra or Oriente and startDate is 1 August and endDate is 31 July and idWorkDay is completa`() {
        val expectedValue = BigDecimal(400).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-08-01"
        val endDate = "2020-07-31"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            SIERRA_ORIENTE
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 285,56 when area is Sierra or Oriente and startDate is 1 August 2019 and endDate is 18  April  2020 and idWorkDay is completa`() {
        val expectedValue = BigDecimal(285.56).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-08-01"
        val endDate = "2020-04-18"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            SIERRA_ORIENTE
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 206,67 when area is Sierra or Oriente and startDate is 25 January 2020 and endDate is 31 July 2020 and idWorkDay is completa`() {
        val expectedValue = BigDecimal(206.67).setScale(2, RoundingMode.HALF_UP)
        val endDate = "2020-07-31"
        val startDate = "2020-01-25"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            SIERRA_ORIENTE
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 166,67 when area is Sierra or Oriente and startDate is 25 January 2019 and endDate is 31 December 2020 and idWorkDay is completa`() {
        val expectedValue = BigDecimal(166.67).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-01-25"
        val endDate = "2020-12-31"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            SIERRA_ORIENTE
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 400,00 when area is Sierra or Oriente and startDate is 01 August 2019 and endDate is 31 July 2021 and idWorkDay is completa`() {
        val expectedValue = BigDecimal(400.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-08-01"
        val endDate = "2021-07-31"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            SIERRA_ORIENTE
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 32,22 when area is Sierra or Oriente and startDate is 01 August 2019 and endDate is 30 August 2021 and idWorkDay is completa`() {
        val expectedValue = BigDecimal(32.22).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-08-01"
        val endDate = "2021-08-30"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            SIERRA_ORIENTE
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 24,17 when area is Sierra or Oriente and startDate is 01 August 2019 and endDate is 30 August 2021 and idWorkDay is 2 parcial`() {
        val expectedValue = BigDecimal(24.17).setScale(2, RoundingMode.HALF_UP)
        val numberHours = 30
        val startDate = "2019-08-01"
        val endDate = "2021-08-30"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            PARCIAL,
            SIERRA_ORIENTE,
            numberHours
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 20,14 when area is Sierra or Oriente and startDate is 01 August 2019 and endDate is 30 August 2021 and idWorkDay is 2 parcial`() {
        val expectedValue = BigDecimal(20.14).setScale(2, RoundingMode.HALF_UP)
        val numberHours = 25
        val startDate = "2019-08-01"
        val endDate = "2021-08-30"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            PARCIAL,
            SIERRA_ORIENTE,
            numberHours
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 127,78 when area is Sierra or Oriente and startDate is 25 August 2019 and endDate is 15  April 2020 and idWorkDay is parcial`() {
        val expectedValue = BigDecimal(127.78).setScale(2, RoundingMode.HALF_UP)
        val numberHours = 20
        val startDate = "2019-08-25"
        val endDate = "2020-04-15"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            PARCIAL,
            SIERRA_ORIENTE,
            numberHours
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 400,00 when area is Costa or Galapagos and startDate is 01 March 2019 and endDate is 29 February 2020 and idWorkDay is complete`() {
        val expectedValue = BigDecimal(400.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-02-29"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            COSTA_GALAPAGOS
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 396,67 when area is Costa or Galapagos and startDate is 01 March 2019 and endDate is 28 February 2020 and idWorkDay is complete`() {
        val expectedValue = BigDecimal(396.67).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-02-28"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            COSTA_GALAPAGOS
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 363,33 when area is Costa or Galapagos and startDate is 01  April 2019 and endDate is 28 February 2020 and idWorkDay is complete`() {
        val expectedValue = BigDecimal(363.33).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-04-01"
        val endDate = "2020-02-28"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            COSTA_GALAPAGOS
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 366,67 when area is Costa or Galapagos and startDate is 15 March 2019 and endDate is 15 February 2020 and idWorkDay is complete`() {
        val expectedValue = BigDecimal(366.67).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-15"
        val endDate = "2020-02-15"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            COSTA_GALAPAGOS
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 36,67 when area is Costa or Galapagos and startDate is 01 March 2019 and endDate is 04  April 2021 and idWorkDay is complete`() {
        val expectedValue = BigDecimal(36.67).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2021-04-04"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            COSTA_GALAPAGOS
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 27,50 when area is Costa or Galapagos and startDate is 01 March 2019 and endDate is 04  April 2021 and idWorkDay is partial`() {
        val expectedValue = BigDecimal(27.50).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2021-04-04"
        val numberOfHours = 30

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            PARCIAL,
            COSTA_GALAPAGOS,
            numberOfHours
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 215,28 when area is Costa or Galapagos and startDate is 05 March 2019 and endDate is 15 April 2020 and idWorkDay is partial time`() {
        val expectedValue = BigDecimal(215.28).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-05"
        val endDate = "2020-01-15"
        val numberOfHours = 25

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            PARCIAL,
            COSTA_GALAPAGOS,
            numberOfHours
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 350,00 when area is Costa or Galapagos and startDate is 01 March 2019 and endDate is 29 February 2020 and idWorkDay is partial time`() {
        val expectedValue = BigDecimal(350.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-02-29"
        val numberOfHours = 35

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            PARCIAL,
            COSTA_GALAPAGOS,
            numberOfHours
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 350,00 when area is Sierra or Oriente and startDate is 01 March 2019 and endDate is 29 February 2020 and idWorkDay is partial time`() {
        val expectedValue = BigDecimal(350.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2017-08-01"
        val endDate = "2020-07-31"
        val numberOfHours = 35

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            PARCIAL,
            SIERRA_ORIENTE,
            numberOfHours
        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 132,22 when area is Sierra or Oriente and startDate is 01 December 2019 and endDate is 30 November 2020 and idWorkDay is complete time`() {
        val expectedValue = BigDecimal(132.22).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-12-01"
        val endDate = "2020-11-30"
//        val numberOfHours = 40

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            SIERRA_ORIENTE

        )

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 66,67 when area is Sierra or Oriente and startDate is 01 January 2019 and endDate is 01 October 2020 and idWorkDay is complete time`() {
        val expectedValue = BigDecimal(66.67).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2020-01-01"
        val endDate = "2020-10-01"

        val result = calculatorsService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            COMPLETA,
            SIERRA_ORIENTE

        )

        assertEquals(expectedValue, result)
    }

    //</editor-fold>
    // <editor-fold desc="APORTE IESS MENSUALIZADO">
    @Test
    fun `should return 37,80 of IESS contribution  when my salary is 400`() {
        val expectedValue = BigDecimal(37.80).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(400.00).setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getAportacionMensualIESS(salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return amount 42,53 of IESS contribution  when my salary is 450`() {
        val expectedValue = BigDecimal(42.53).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getAportacionMensualIESS(salary)

        assertEquals(expectedValue, result)
    }

    //</editor-fold>
    // <editor-fold desc="FONDOS DE RESERVA MENSUALIZADO">
    @Test
    fun `should return 0 of fondo de reserva contribution when period is less than one year`() {
        val expectedValue = BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-01-01"
        val endDate = "2019-12-30"

        val result = calculatorsService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 37,49 of fondo de reserva contribution when period is one year`() {
        val expectedValue = BigDecimal(0).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-01-01"
        val endDate = "2020-01-01"

        val result = calculatorsService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 0,00 of fondo de reserva contribution when period is more than one year`() {
        val expectedValue = BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(400.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-03-01"

        val result = calculatorsService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 24,99 of fondo de reserva contribution when period is more than one year`() {
        val expectedValue = BigDecimal(24.99).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(300.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-03-02"

        val result = calculatorsService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    //</editor-fold>
    // <editor-fold desc="VACACIONES ANUALES">

    @Test
    fun `should return annual leave days 15 when user is greater or equal 18 years of age `() {
        val expectedNumberOfDays = 15
        val years = 19

        val result = calculatorsService.numberOfAnnualLeavesPerAge(years)

        assertEquals(expectedNumberOfDays, result)
    }

    @Test
    fun `should return annual leave days 20 when user is under the age of 16`() {
        val expectedNumberOfDays = 20
        val years = 15

        val result = calculatorsService.numberOfAnnualLeavesPerAge(years)

        assertEquals(expectedNumberOfDays, result)
    }

    fun getAgeBetween16and17(): List<Int> {
        return listOf(16, 17)
    }

    @Test
    @Parameters(method = "getAgeBetween16and17")
    @TestCaseName("{method}_with_age_param_{params}")
    fun `should return annual leave days 18 when user is between 16 and 18 years old `(age: Int) {
        val expectedNumberOfDays = 18

        val result = calculatorsService.numberOfAnnualLeavesPerAge(age)

        assertEquals(expectedNumberOfDays, result)
    }


    @Test
    fun `should return annual leave value equivalent to one day when user is under the age of 16`() {
        val numberOfDays = 20
        val expectedPercentageDay = numberOfDays.toDouble().div(360.toDouble())

        val result = calculatorsService.equivalentOfAnnualLeavesToDay(numberOfDays)

        assertEquals(expectedPercentageDay, result)
    }

    @Test
    fun `should return annual leave value equivalent to one day when user is between 16 and 18 years old`() {
        val numberOfDays = 18
        val expectedPercentageDay = numberOfDays.toDouble().div(360.toDouble())

        val result = calculatorsService.equivalentOfAnnualLeavesToDay(numberOfDays)

        assertEquals(expectedPercentageDay, result)
    }

    @Test
    fun `should return annual leave value equivalent to one day when user is over the age of 18`() {
        val numberOfDays = 15
        val expectedPercentageDay = numberOfDays.toDouble().div(360.toDouble())

        val result = calculatorsService.equivalentOfAnnualLeavesToDay(numberOfDays)

        assertEquals(expectedPercentageDay, result)
    }

    @Test
    fun `should return annual leave days 18,0 per year when user is 16 age and has one year`() {
        val age = 16
        val startDate = "2019-08-01"
        val endDate = "2020-08-01"
        val expectedNumberAnnualLeaveDays = 18.0

        val result = calculatorsService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }

    @Test
    fun `should return annual leave days 15,0 per year when user is 19 age and has one year`() {
        val age = 19
        val startDate = "2019-08-01"
        val endDate = "2020-08-01"
        val expectedNumberAnnualLeaveDays = 15.0

        val result = calculatorsService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }

    @Test
    fun `should return annual leave days 5,0 per year when user is 19 age and has 4 months`() {
        val age = 19
        val startDate = "2019-08-01"
        val endDate = "2019-12-01"
        val expectedNumberAnnualLeaveDays = 5.0

        val result = calculatorsService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }

    @Test
    fun `should return annual leave days 5,58 per year when user is 19 age and has 4 months`() {
        val age = 19
        val startDate = "2019-08-01"
        val endDate = "2019-12-15"
        val expectedNumberAnnualLeaveDays = 5.583333333333333

        val result = calculatorsService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }

    @Test
    fun `should return annual leave days 7 per year when user is 19 age and has 4 months`() {
        val age = 19
        val startDate = "2019-01-01"
        val endDate = "2019-12-31"
        val expectedNumberAnnualLeaveDays = 15.0

        val result = calculatorsService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }


    @Test
    fun `should return the new start date 2019-02-01 with last year when number of days is over 360 days`() {
        val startDate = "2018-02-01"
        val endDate = "2019-08-31"
        val expectedStartDate = "2019-02-01"

        val result = calculatorsService.newStartDatePeriodOfWork(startDate, endDate)

        assertEquals(expectedStartDate, result)
    }

    @Test
    fun `should return the new start date with last year when number of days is over 720 days`() {
        val startDate = "2017-02-01"
        val endDate = "2019-08-31"
        val expectedStartDate = "2019-02-01"

        val result = calculatorsService.newStartDatePeriodOfWork(startDate, endDate)

        assertEquals(expectedStartDate, result)
    }

    @Test
    fun `should return the new start date with last year when number of days is over 3 years`() {
        val startDate = "2010-02-01"
        val endDate = "2015-08-31"
        val expectedStartDate = "2015-02-01"

        val result = calculatorsService.newStartDatePeriodOfWork(startDate, endDate)

        assertEquals(expectedStartDate, result)
    }

    @Test
    fun `should return annual leave days 15,0 per year when user is 19 age and has 4 months`() {
        val age = 19
        val startDate = "2018-02-01"
        val endDate = "2019-08-31"
        val expectedNumberAnnualLeaveDays = 8.75

        val result = calculatorsService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }

    fun getTestCases(): ArrayList<EntriesAnnualLeaveTest> {
        var annualLeaveTestCases: ArrayList<EntriesAnnualLeaveTest> = arrayListOf()
        annualLeaveTestCases.add(EntriesAnnualLeaveTest(19, "2018-02-01", "2019-08-31", 8.75))
        annualLeaveTestCases.add(EntriesAnnualLeaveTest(18, "2018-02-01", "2019-08-31", 8.75))
        annualLeaveTestCases.add(EntriesAnnualLeaveTest(17, "2018-02-01", "2019-08-31", 10.5))
        annualLeaveTestCases.add(EntriesAnnualLeaveTest(16, "2018-02-01", "2019-08-31", 10.5))
        annualLeaveTestCases.add(EntriesAnnualLeaveTest(15, "2018-02-01", "2019-08-31", 11.67))
        annualLeaveTestCases.add(
            EntriesAnnualLeaveTest(
                15,
                "2018-02-01",
                "2018-02-15",
                0.777777778
            )
        )
        return annualLeaveTestCases
    }

    @Test
    @Parameters(method = "getTestCases")
    @TestCaseName("{method} with params {params}")
    fun `should return annual leave days`(
        entriesAnnualLeaveTest: EntriesAnnualLeaveTest
    ) {
        val result = calculatorsService.getNumberAnnualLeaveDay(
            entriesAnnualLeaveTest.startDate,
            entriesAnnualLeaveTest.endDate,
            entriesAnnualLeaveTest.age
        )
        assertEquals(
            entriesAnnualLeaveTest.result.toBigDecimal().setScale(2, RoundingMode.HALF_UP),
            result.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        )
    }

    @Test
    fun `should return equivalent salary per day when salary is 400`() {
        val salary = 400

        val expectedEquivalentSalaryPerDay = BigDecimal.valueOf(13.3333333333)

        val result = calculatorsService.getDailyPayAnnualLeave(salary.toDouble())

        assertEquals(
            expectedEquivalentSalaryPerDay.setScale(2, RoundingMode.HALF_UP),
            result.setScale(2, RoundingMode.HALF_UP)
        )
    }

    @Test
    fun `should return equivalent salary per day when salary is 500`() {
        val salary = 500

        val expectedEquivalentSalaryPerDay = BigDecimal.valueOf(16.6666666667)

        val result = calculatorsService.getDailyPayAnnualLeave(salary.toDouble())

        assertEquals(
            expectedEquivalentSalaryPerDay.setScale(2, RoundingMode.HALF_UP),
            result.setScale(2, RoundingMode.HALF_UP)
        )
    }

    @Test
    fun `should return equivalent total cost of vacations`() {
        val salary = 500
        val age = 19
        val startDate = "2018-02-01"
        val endDate = "2019-08-31"
        val daysTaked = 0

        val expectedEquivalentSalaryPerDay = BigDecimal.valueOf(145.8333333)

        val result =
            calculatorsService.getCostOfAnnualLeaveDays(
                startDate,
                endDate,
                salary.toDouble(),
                age,
                daysTaked
            )

        assertEquals(
            expectedEquivalentSalaryPerDay.setScale(2, RoundingMode.HALF_UP),
            result.setScale(2, RoundingMode.HALF_UP)
        )
    }

    @Test
    fun `should return equivalent total cost of annual leave days not taked`() {
        val salary = 500
        val age = 19
        val startDate = "2018-02-01"
        val endDate = "2019-08-31"
        val daysTaken = 3

        val expectedEquivalentSalaryPerDay = BigDecimal.valueOf(95.83333333)

        val result =
            calculatorsService.getCostOfAnnualLeaveDays(
                startDate,
                endDate,
                salary.toDouble(),
                age,
                daysTaken
            )

        assertEquals(
            expectedEquivalentSalaryPerDay.setScale(2, RoundingMode.HALF_UP),
            result.setScale(2, RoundingMode.HALF_UP)
        )
    }

    @Test
    fun `should return current age of 18 with 2002-02-01`() {
        val birthDate = "2002-02-01"
        val expectedAge = 18

        val result = calculatorsService.getAge(birthDate)

        assertEquals(expectedAge, result)
    }

    @Test
    fun `should return age 17`() {
        val birthDate = "2002-02-01"
        val startDate = "2019-10-01"
        val expectedAge = 17

        val result = calculatorsService.getAge(birthDate, startDate)

        assertEquals(expectedAge, result)
    }

    @Test
    fun `should return age 18`() {
        val birthDate = "2002-02-01"
        val endDate = "2020-08-31"
        val expectedAge = 18

        val result = calculatorsService.getAge(birthDate, endDate)

        assertEquals(expectedAge, result)
    }

    @Test
    fun `should return true if birthday is on`() {
        val birthDate = "2002-02-01"
        val startDate = "2019-10-01"
        val endDate = "2020-08-31"

        val result = calculatorsService.isBirthdayInPeriodOfWork(startDate, endDate, birthDate)

        assert(result)
    }

    @Test
    fun `should return false if birthday is not on range`() {
        val birthDate = "2002-02-27"
        val startDate = "2020-03-01"
        val endDate = "2020-10-01"

        val result = calculatorsService.isBirthdayInPeriodOfWork(startDate, endDate, birthDate)

        assertFalse(result)
    }

    @Test
    fun `should return false if birthday not change in period of work`() {
        val birthDate = "2002-03-01"
        val startDate = "2020-03-01"
        val endDate = "2020-10-01"

        val result = calculatorsService.isBirthdayInPeriodOfWork(startDate, endDate, birthDate)

        assertFalse(result)
    }

    @Test
    fun `should return true if birth date is in end date period of work  `() {
        val birthDate = "2002-10-01"
        val startDate = "2020-03-01"
        val endDate = "2020-10-01"

        val result = calculatorsService.isBirthdayInPeriodOfWork(startDate, endDate, birthDate)

        assert(result)
    }

    @Test
    fun `should return birthday like new end date period of work when birthday is the same year of end date`() {
        val birthDate = "2002-02-01"
        val startDate = "2019-10-01"
        val endDate = "2020-10-01"
        val expectedEndDate = "2020-02-01"

        val result = calculatorsService.getNewDate(startDate, endDate, birthDate)

        assertEquals(expectedEndDate, result)
    }

    @Test
    fun `should return birthday like new end date period of work when birthday is in the same year of start date`() {
        val birthDate = "2002-12-01"
        val startDate = "2019-10-01"
        val endDate = "2020-10-01"
        val expectedEndDate = "2019-12-01"

        val result = calculatorsService.getNewDate(startDate, endDate, birthDate)

        assertEquals(expectedEndDate, result)
    }

    @Test
    fun `should return birthday like new end date period of work when birthday is one day after start date`() {
        val birthDate = "2002-10-02"
        val startDate = "2019-10-01"
        val endDate = "2020-10-01"
        val expectedEndDate = "2019-10-02"

        val result = calculatorsService.getNewDate(startDate, endDate, birthDate)

        assertEquals(expectedEndDate, result)
    }

    @Test
    fun `should return the same start date period of work when birthday is equals to startDate`() {
        val birthDate = "2002-10-01"
        val startDate = "2019-10-01"
        val endDate = "2020-10-01"
        val expectedEndDate = "2020-10-01"

        val result = calculatorsService.getNewDate(startDate, endDate, birthDate)

        assertEquals(expectedEndDate, result)
    }

    @Test
    fun `should return the same end date period of work when birthday is equals to endDate`() {
        val birthDate = "2002-10-01"
        val startDate = "2019-11-01"
        val endDate = "2020-10-01"
        val expectedEndDate = "2020-10-01"

        val result = calculatorsService.getNewDate(startDate, endDate, birthDate)

        assertEquals(expectedEndDate, result)
    }

    @Test
    fun `should return total cost of first period of work when birthday is one day after startDate`() {
        val birthDate = "2002-11-02"
        val startDate = "2019-11-01"
        val endDate = "2020-11-01"
        val salary = 500.toDouble()
        val daysTaken = 0

        val expectedCostByPeriod = BigDecimal.valueOf(0.833333333).setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getTotalCostFirstPeriod(
            startDate,
            endDate,
            birthDate,
            salary,
            daysTaken
        )

        assertEquals(expectedCostByPeriod, result)
    }

    @Test
    fun `should return total cost of first period of work when birthday is two months after startDate`() {
        val birthDate = "2002-12-02"
        val startDate = "2019-11-01"
        val endDate = "2020-11-01"
        val salary = 500.toDouble()
        val daysTaken = 0
        val expectedCostByPeriod = BigDecimal.valueOf(25.8333333).setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getTotalCostFirstPeriod(
            startDate,
            endDate,
            birthDate,
            salary,
            daysTaken
        )

        assertEquals(expectedCostByPeriod, result)
    }

    @Test
    fun `should return total cost of complete period of work when birthday is equals to startDate`() {
        val birthDate = "2002-11-01"
        val startDate = "2020-11-01"
        val endDate = "2021-11-01"
        val salary = 500.toDouble()
        val daysTaken = 0
        val expectedCostByPeriod = BigDecimal.valueOf(250).setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getTotalCostFirstPeriod(
            startDate,
            endDate,
            birthDate,
            salary,
            daysTaken
        )

        assertEquals(expectedCostByPeriod, result)
    }

    @Test
    fun `should return total cost of complete period of work when birthday is equals to endDate`() {
        val birthDate = "2002-12-01"
        val startDate = "2020-01-01"
        val endDate = "2020-12-01"
        val salary = 500.toDouble()
        val daysTaken = 0
        val expectedCostByPeriod = BigDecimal.valueOf(275).setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getTotalCostFirstPeriod(
            startDate,
            endDate,
            birthDate,
            salary,
            daysTaken
        )

        assertEquals(expectedCostByPeriod, result)
    }

    @Test
    fun `should return total cost of first period of work when birthday is in the middle`() {
        val birthDate = "2002-04-01"
        val startDate = "2020-01-01"
        val endDate = "2020-12-01"
        val salary = 500.toDouble()
        val daysTaken = 0
        val expectedCostByPeriod = BigDecimal.valueOf(75).setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getTotalCostFirstPeriod(
            startDate,
            endDate,
            birthDate,
            salary,
            daysTaken
        )

        assertEquals(expectedCostByPeriod, result)
    }

    @Test
    fun `should return total cost of second period of work when birthday is in the middle`() {
        val birthDate = "2002-04-01"
        val startDate = "2020-01-01"
        val endDate = "2020-12-01"
        val salary = 500.toDouble()
        val daysTaken = 0
        val expectedCostByPeriod = BigDecimal.valueOf(166.6666667).setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getTotalCostSecondPeriod(
            startDate,
            endDate,
            birthDate,
            salary,
            daysTaken
        )

        assertEquals(expectedCostByPeriod, result)
    }

    @Test
    fun `should return total cost of complete period of work when birthday is in the middle`() {
        val birthDate = "2002-04-01"
        val startDate = "2020-01-01"
        val endDate = "2020-12-01"
        val salary = 500.toDouble()
        val daysTaken = 0
        val expectedCostByPeriod = BigDecimal.valueOf(241.6666667).setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getTotalCostPeriod(
            startDate,
            endDate,
            birthDate,
            salary,
            daysTaken
        )

        assertEquals(expectedCostByPeriod, result)
    }

    @Test
    fun `should return total cost of complete period of work when birthday is out of period of work`() {
        val birthDate = "2002-12-15"
        val startDate = "2020-01-01"
        val endDate = "2020-12-01"
        val salary = 500.toDouble()
        val daysTaken = 0
        val expectedCostByPeriod = BigDecimal.valueOf(275).setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getTotalCostPeriod(
            startDate,
            endDate,
            birthDate,
            salary,
            daysTaken
        )

        assertEquals(expectedCostByPeriod, result)
    }

    @Test
    fun `should return total cost of complete period of work when birthday is out of period of work(different years)`() {
        val birthDate = "2002-10-15"
        val startDate = "2019-08-01"
        val endDate = "2020-07-01"
        val salary = 500.toDouble()
        val daysTaken = 0
        val expectedCostByPeriod = BigDecimal.valueOf(275).setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getTotalCostPeriod(
            startDate,
            endDate,
            birthDate,
            salary,
            daysTaken
        )

        assertEquals(expectedCostByPeriod, result)
    }

    //</editor-fold>
}
