package com.untha.applicationservices

import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Method
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*


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
            calculatorsService.calculateDecimoTercerSueldoMensualizado(salary.toBigDecimal())
        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 37,5 decimo tercero mensualizado when receive a salary of 450`() {
        val salary = 450
        val expectedValue = BigDecimal(37.5).setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.calculateDecimoTercerSueldoMensualizado(salary.toBigDecimal())

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

        val result = calculatorsService.calculateDecimoCuartoSueldoMensualizado()

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
    // <editor-fold desc="VACACIONES">

    @Test
    fun `should return 15,00 when startDate is 1 de August 2019 and endDate is 1 August 2020`() {
        val expectedValue = BigDecimal(15.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-08-01"
        val endDate = "2020-08-01"

        val result = calculatorsService.getVacations(startDate, endDate)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 16,25 when startDate is 1 de July 2019 and endDate is 1 August 2020`() {
        val expectedValue = BigDecimal(16.25).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-07-01"
        val endDate = "2020-08-01"

        val result = calculatorsService.getVacations(startDate, endDate)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 12,5 when startDate is 1 de July 2019 and endDate is 1 May 2020`() {
        val expectedValue = BigDecimal(12.5).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-07-01"
        val endDate = "2020-05-01"

        val result = calculatorsService.getVacations(startDate, endDate)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 14,88 when startDate is 1 de March 2020 and endDate is 28 February 2020`() {
        val expectedValue = BigDecimal(14.88).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2020-03-01"
        val endDate = "2021-02-28"

        val result = calculatorsService.getVacations(startDate, endDate)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 35 when startDate is 1 de March 2020 and endDate is 01 July 2022`() {
        val expectedValue = BigDecimal(35.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2020-03-01"
        val endDate = "2022-07-01"

        val result = calculatorsService.getVacations(startDate, endDate)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 45 when startDate is 1 de March 2020 and endDate is 01 March 2023`() {
        val expectedValue = BigDecimal(45.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2020-03-01"
        val endDate = "2023-03-01"

        val result = calculatorsService.getVacations(startDate, endDate)

        assertEquals(expectedValue, result)
    }

    //</editor-fold>
}
