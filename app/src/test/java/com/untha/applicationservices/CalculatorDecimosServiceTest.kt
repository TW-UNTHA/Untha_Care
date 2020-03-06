package com.untha.applicationservices

import com.untha.utils.calculateNumberOfDayBetween
import com.untha.utils.salaryEquivalentPerDay
import com.untha.utils.salaryForDaysWorked
import com.untha.utils.startDateOfMonth
import com.untha.utils.stringToCalendar
import junit.framework.Assert.assertEquals
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@RunWith(JUnitParamsRunner::class)
class CalculatorDecimosServiceTest {
    val calculatorsService = CalculatorDecimosService()

    companion object {
        const val SIERRA_ORIENTE = 2
        const val COSTA_GALAPAGOS = 1
        const val COMPLETA = 1
        const val PARCIAL = 2
    }

    @Test
    fun `should transform String to Calendar`() {
        val stringDate = "2012-02-23"
        val calendarDate = stringToCalendar(stringDate)
        assertThat(calendarDate, instanceOf(Calendar::class.java))
    }
    // <editor-fold desc="CALCULATE NUMBER OF DAYS">

    @Test
    fun `should return 360  when start date 01 January 2020 and end date 01 January 2021`() {
        val calculatorsService = CalculatorDecimosService()
        val startDate = "2020-01-01"
        val endDate = "2021-01-01"
        val expectedValue = 360

        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)

        val numberOfDays =
            calculateNumberOfDayBetween(calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 44 when start date 1 December 2019 and end date 15 January 2020 `() {
        val calculatorsService = CalculatorDecimosService()
        val startDate = "2019-12-01"
        val endDate = "2020-01-15"
        val expectedValue = 44

        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)

        val numberOfDays =
            calculateNumberOfDayBetween(calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 163 when start date 1 December 2019 and end date 14 May 2020 `() {
        val startDate = "2019-12-01"
        val endDate = "2020-05-14"
        val expectedValue = 163

        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)

        val numberOfDays =
            calculateNumberOfDayBetween(calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }


    @Test
    fun `should return 329 when start date 1 January 2020 and end date 30 November 2020 `() {
        val startDate = "2020-01-01"
        val endDate = "2020-11-30"
        val expectedValue = 329

        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)

        val numberOfDays =
            calculateNumberOfDayBetween(calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 160 when start date 21 February 2020 and end date 31 July 2020 `() {
        val startDate = "2020-02-21"
        val endDate = "2020-07-31"
        val expectedValue = 160

        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)

        val numberOfDays =
            calculateNumberOfDayBetween(calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 133 when start date 16 October 2019 and end date 29 February 2020 `() {
        val startDate = "2019-10-16"
        val endDate = "2020-02-29"
        val expectedValue = 133

        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)

        val numberOfDays =
            calculateNumberOfDayBetween(calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 132 when start date 16 October 2020 and end date 28 February 2021 `() {
        val startDate = "2020-10-16"
        val endDate = "2021-02-28"
        val expectedValue = 132

        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)

        val numberOfDays =
            calculateNumberOfDayBetween(calendarStartDate, calendarEndDate)

        assertEquals(expectedValue, numberOfDays)
    }

    //</editor-fold>
    // <editor-fold desc="DECIMO TERCERO MENSUALIZADO">
    @Test
    fun `should return first day of month mixed with year and month of end date`() {
        val endDate = "2019-02-15"
        val expectedStartDate = "2019-02-01"

        val result = startDateOfMonth(endDate)

        assertEquals(expectedStartDate, result)
    }

    fun dataMonthlyChristmasBonus(): List<String> {
        return listOf("2019-02-10", "2019-02-15", "2019-02-20", "2019-02-25", "2019-02-28")
    }

    @Test
    @Parameters(method = "dataMonthlyChristmasBonus")
    @TestCaseName("{method}_with_date_param_{params}")
    fun `should return first day of month`(endDate: String) {
        val expectedStartDate = "2019-02-01"

        val result = startDateOfMonth(endDate)

        assertEquals(expectedStartDate, result)
    }

    @Test
    fun `should return salary equivalent per day`() {
        val salary = 500.toBigDecimal()
        val expectedCostPerDay = BigDecimal.valueOf(16.6666666667).setScale(2, RoundingMode.HALF_UP)

        val result = salaryEquivalentPerDay(salary).setScale(2, RoundingMode.HALF_UP)

        assertEquals(expectedCostPerDay, result)
    }

    @Test
    fun `should return salary total of month`() {
        val salary = 500.toBigDecimal()
        val startDate = "2019-01-01"
        val endDate = "2019-01-15"
        val expectedCostPerDay =
            BigDecimal.valueOf(250).setScale(2, RoundingMode.HALF_UP)

        val result =
            salaryForDaysWorked(startDate, endDate, salary)
                .setScale(2, RoundingMode.HALF_UP)

        assertEquals(expectedCostPerDay, result)
    }

    @Test
    fun `should return salary total of month with no days of month before`() {
        val salary = 500.toBigDecimal()
        val startDate = "2019-01-01"
        val endDate = "2019-02-15"
        val expectedCostPerDay =
            BigDecimal.valueOf(250).setScale(2, RoundingMode.HALF_UP)

        val result =
            salaryForDaysWorked(startDate, endDate, salary)
                .setScale(2, RoundingMode.HALF_UP)

        assertEquals(expectedCostPerDay, result)
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

    @Test
    fun `should return cost of days complete time`() {
        val startDate = "2019-02-28"
        val endDate = "2020-02-15"
        val hoursWorked = 40
        val expectedCost = 16.67.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoCuartoSueldoMensualizadoFiniquito(
                startDate,
                endDate,
                hoursWorked
            )

        assertEquals(expectedCost, result)
    }

    @Test
    fun `should return cost of days with date  2020-02-17 complete time`() {
        val startDate = "2019-02-28"
        val endDate = "2020-02-17"
        val hoursWorked = 40
        val expectedCost = 18.89.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoCuartoSueldoMensualizadoFiniquito(
                startDate,
                endDate,
                hoursWorked
            )

        assertEquals(expectedCost, result)
    }

    @Test
    fun `should return cost of days with date  2020-03-01 complete time`() {
        val startDate = "2019-02-28"
        val endDate = "2020-03-01"
        val hoursWorked = 40
        val expectedCost = 1.11.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoCuartoSueldoMensualizadoFiniquito(
                startDate,
                endDate,
                hoursWorked
            )

        assertEquals(expectedCost, result)
    }

    @Test
    fun `should return cost of days with date 2020-03-30 complete time`() {
        val startDate = "2019-02-28"
        val endDate = "2020-01-30"
        val hoursWorked = 40
        val expectedCost = 33.33.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoCuartoSueldoMensualizadoFiniquito(
                startDate,
                endDate,
                hoursWorked
            )

        assertEquals(expectedCost, result)
    }

    @Test
    fun `should return cost of days with date 2020-01-31 complete time`() {
        val startDate = "2019-02-28"
        val endDate = "2020-01-31"
        val hoursWorked = 40
        val expectedCost = 33.33.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoCuartoSueldoMensualizadoFiniquito(
                startDate,
                endDate,
                hoursWorked
            )

        assertEquals(expectedCost, result)
    }

    @Test
    fun `should return cost of days with date 2020-02-29 complete time`() {
        val startDate = "2020-02-28"
        val endDate = "2020-02-29"
        val hoursWorked = 40
        val expectedCost = 3.33.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoCuartoSueldoMensualizadoFiniquito(
                startDate,
                endDate,
                hoursWorked
            )

        assertEquals(expectedCost, result)
    }

    @Test
    fun `should return cost of days with start date 2019-02-02 end date 2019-02-28 partial time`() {
        val startDate = "2019-02-02"
        val endDate = "2019-02-28"
        val hoursWorked = 20
        val expectedCost = 16.11.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoCuartoSueldoMensualizadoFiniquito(
                startDate,
                endDate,
                hoursWorked
            )

        assertEquals(expectedCost, result)
    }

    @Test
    fun `should return cost of days with start date "2019-04-03" end date "2019-04-27" complete time`() {
        val startDate = "2019-04-03"
        val endDate = "2019-04-27"
        val hoursWorked = 40
        val expectedCost = 27.78.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoCuartoSueldoMensualizadoFiniquito(
                startDate,
                endDate,
                hoursWorked
            )

        assertEquals(expectedCost, result)
    }

    @Test
    fun `should return cost of days with start date "2019-04-03" end date "2019-04-27" partial time`() {
        val startDate = "2019-04-03"
        val endDate = "2019-04-27"
        val hoursWorked = 20
        val expectedCost = 13.89.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoCuartoSueldoMensualizadoFiniquito(
                startDate,
                endDate,
                hoursWorked
            )

        assertEquals(expectedCost, result)
    }

    @Test
    fun `should return cost of days worked decimo tercero`() {
        val startDate = "2019-01-01"
        val endDate = "2019-02-28"
        val salary = 400.toDouble()
        val expectedCost = 33.33.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoTercerSueldoFiniquitoMensualizado(
                startDate,
                endDate,
                salary
            )

        assertEquals(expectedCost, result)
    }

    @Test
    fun `should return cost of days worked decimo tercero startDate = "2019-02-02" endDate = "2019-02-28" `() {
        val startDate = "2019-02-02"
        val endDate = "2019-02-28"
        val salary = 400.toDouble()
        val expectedCost = 32.22.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoTercerSueldoFiniquitoMensualizado(
                startDate,
                endDate,
                salary
            )

        assertEquals(expectedCost, result)
    }

    @Test
    fun `should return cost of days worked decimo tercero 2019-08-15 2019-08-31`() {
        val startDate = "2019-08-15"
        val endDate = "2019-08-31"
        val salary = 400.toDouble()
        val expectedCost = 17.78.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =
            calculatorsService.getDecimoTercerSueldoFiniquitoMensualizado(
                startDate,
                endDate,
                salary
            )

        assertEquals(expectedCost, result)
    }
}
