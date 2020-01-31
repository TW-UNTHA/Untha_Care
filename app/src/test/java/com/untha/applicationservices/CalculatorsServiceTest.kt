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
        var stringDate = "2012-02-23"
        var calendarDate = stringToCalendar.invoke(calculatorsService, stringDate)
        assertThat(calendarDate, instanceOf(Calendar::class.java))
    }

    @Test
    fun `should return 360  when start date 01 Jan 2020 and end date 01 Jan 2021`() {
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
    fun `should return 44 when start date 1 dic 2019 and end date 15 jan 2020 `() {
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
    fun `should return 163 when start date 1 dic 2019 and end date 14 may 2020 `() {
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
    fun `should return 329 when start date 1 jan 2020 and end date 30 nov 2020 `() {
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
    fun `should return 160 when start date 21 feb 2020 and end date 31 jul 2020 `() {
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
    fun `should return 133 when start date 16 oct 2019 and end date 29 feb 2020 `() {
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
    fun `should return 132 when start date 16 oct 2020 and end date 28 feb 2021 `() {
        val startDate = "2020-10-16"
        val endDate = "2021-02-28"
        val expectedValue = 132

        val calendarStartDate = stringToCalendar.invoke(calculatorsService, startDate)
        val calendarEndDate = stringToCalendar.invoke(calculatorsService, endDate)

        val numberOfDays =
            calculateDaysBetween.invoke(calculatorsService, calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return decimo tercero mensualizado = 33,33 when receive a salary of 400`() {
        val salary = 400
        var expectedValue = BigDecimal(33.33)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.calculateDecimoTercerSueldoMensualizado(salary.toBigDecimal())
        assertEquals(expectedValue, result)


    }

    @Test
    fun `should return 37,5 decimo tercero mensualizado when receive a salary of 450`() {
        val salary = 450
        var expectedValue = BigDecimal(37.5)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.calculateDecimoTercerSueldoMensualizado(salary.toBigDecimal())
        assertEquals(expectedValue, result)


    }

    @Test
    fun `should return 500 when start date is 1 dic and end date is 30 of nov and salary is 500`() {
        val salary = 500
        var expectedValue = BigDecimal(500)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            "2019-12-01",
            "2020-11-30"
        )
        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 48,89 when start date is 1st dic 2019 and end date is 15 jan 2020 and salary is 400`() {
        val salary = 400
        var expectedValue = BigDecimal(48.89)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            "2019-12-01",
            "2020-01-15"
        )
        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return 181,11 when start date is 1st dic 2019 and end date is 14 may 2020 and salary is 400`() {
        val salary = 400
        var expectedValue = BigDecimal(181.11)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            "2019-12-01",
            "2020-05-14"
        )
        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return 400 when start date is 1st dic 2019 and end date is 30 nov 2022 and salary is 400`() {
        val salary = 400
        var expectedValue = BigDecimal(400)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            "2019-12-01",
            "2022-11-30"
        )
        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return 365,56 when start date is 1 jan 2020 and end date is 30 nov 2020 and salary is 400`() {
        val salary = 400
        var expectedValue = BigDecimal(365.56)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            "2020-01-01",
            "2020-11-30"
        )
        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return 122,22 when start date is 21 mar 2020 and end date is 11 jul 2020 and salary is 400`() {
        val salary = 400
        var expectedValue = BigDecimal(122.22)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            "2020-03-21",
            "2020-07-11"
        )
        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return 145,56 when start date is 31 jul 2018 and end date is 12 de apr 2020 and salary is 400`() {
        val salary = 400
        var expectedValue = BigDecimal(145.56)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            "2018-07-31",
            "2020-04-12"
        )
        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return 32,22 when start date is 15 Ag 2019 and end date is 30 de dic 2020 and salary is 400`() {
        val salary = 400
        var expectedValue = BigDecimal(32.22)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            "2019-08-15",
            "2020-12-30"
        )
        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return decimo cuarto mensualizado 33,33 when SBU is 400`() {
        var expectedValue = BigDecimal(33.33)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.calculateDecimoCuartoSueldoMensuaizado()

        assertEquals(expectedValue, result)


    }

    @Test
    fun `should return SBU 400 when area is Sierra or Oriente and startDate is 1 Ago and endDate is 31 Jul and idWorkDay is 1 completa`() {
        var expectedValue = BigDecimal(400)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoCuartoAcumulado("2019-08-01", "2020-07-31", 1, 2)

        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return 285,56 when area is Sierra or Oriente and startDate is 1 Ago 2019 and endDate is 18 April  2020 and idWorkDay is 1 completa`() {
        var expectedValue = BigDecimal(285.56)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoCuartoAcumulado("2019-08-01", "2020-04-18", 1, 2)

        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return 206,67 when area is Sierra or Oriente and startDate is 25 Jan 2020 and endDate is 31 JUl 2020 and idWorkDay is 1 completa`() {
        var expectedValue = BigDecimal(206.67)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoCuartoAcumulado("2020-01-25", "2020-07-31", 1, 2)

        assertEquals(expectedValue, result)

    }

    @Test
    fun `should return 166,67 when area is Sierra or Oriente and startDate is 25 Jan 2019 and endDate is 31 dic 2020 and idWorkDay is 1 completa`() {
        var expectedValue = BigDecimal(166.67)
        expectedValue = expectedValue.setScale(2, RoundingMode.HALF_UP)

        val result = calculatorsService.getDecimoCuartoAcumulado("2019-01-25", "2020-12-31", 1, 2)

        assertEquals(expectedValue, result)

    }
}
