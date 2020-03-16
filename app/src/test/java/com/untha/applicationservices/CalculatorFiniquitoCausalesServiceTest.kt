package com.untha.applicationservices

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.RoundingMode

class CalculatorFiniquitoCausalesServiceTest {
    val calculatorFiniquitoCausalesService = CalculatorFiniquitoCausalesService()
    @Test
    fun `should return 25% of the salary if a year was completed `() {
        val salary = 500.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result = 125.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val startDate = "2018-04-25"
        val endDate = "2019-04-26"
        val hint = "R3P2R1"
        assertEquals(
            result,
            calculatorFiniquitoCausalesService.getCausalRetribution(
                hint,
                salary,
                startDate,
                endDate
            )
        )

    }

    @Test
    fun `should return 25% of the salary = 600 if a year was completed `() {
        val salary = 600.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result = 150.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val startDate = "2018-04-25"
        val endDate = "2019-04-26"
        val hint = "R3P2R1"
        assertEquals(
            result,
            calculatorFiniquitoCausalesService.getCausalRetribution(
                hint,
                salary,
                startDate,
                endDate
            )
        )

    }

    @Test
    fun `should return 50% of the salary = 600 if two years were completed `() {
        val salary = 600.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result = 300.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val startDate = "2017-04-25"
        val endDate = "2019-04-26"
        val hint = "R3P2R1"
        assertEquals(
            result,
            calculatorFiniquitoCausalesService.getCausalRetribution(
                hint,
                salary,
                startDate,
                endDate
            )
        )

    }

    @Test
    fun `should return 0  if causal is periodo de prueba `() {
        val salary = 600.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result = 0.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val startDate = "2017-04-15"
        val endDate = "2019-04-30"
        val hint = "R3P2R2"
        assertEquals(
            result,
            calculatorFiniquitoCausalesService.getCausalRetribution(
                hint,
                salary,
                startDate,
                endDate
            )
        )
    }

    @Test
    fun `should return 0  if causal is muerte del trabajador `() {
        val salary = 600.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result = 0.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val startDate = "2017-04-15"
        val endDate = "2019-05-30"
        val hint = "R3P2R3"
        assertEquals(
            result,
            calculatorFiniquitoCausalesService.getCausalRetribution(
                hint,
                salary,
                startDate,
                endDate
            )
        )
    }

    @Test
    fun `should return 3 salaries  if causal is despido intempestivo before 3 years  `() {
        val salary = 400.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result = 1200.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val startDate = "2017-04-15"
        val endDate = "2020-01-15"
        val hint = "R3P2R5"
        assertEquals(
            result,
            calculatorFiniquitoCausalesService.getCausalRetribution(
                hint,
                salary,
                startDate,
                endDate
            )
        )
    }


    @Test
    fun `should return 4 salaries  if causal is despido intempestivo and worked 3 years and one day  `() {
        val salary = 400.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result = 1600.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val startDate = "2017-04-15"
        val endDate = "2020-04-16"
        val hint = "R3P2R5"
        assertEquals(
            result,
            calculatorFiniquitoCausalesService.getCausalRetribution(
                hint,
                salary,
                startDate,
                endDate
            )
        )
    }

    @Test
    fun `should return 4 salaries  if causal is despido intempestivo and worked 3 years and 3 months  `() {
        val salary = 400.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result = 1600.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val startDate = "2017-04-15"
        val endDate = "2020-07-15"
        val hint = "R3P2R5"
        assertEquals(
            result,
            calculatorFiniquitoCausalesService.getCausalRetribution(
                hint,
                salary,
                startDate,
                endDate
            )
        )
    }

    @Test
    fun `should return 25 salaries  if causal is despido intempestivo and worked 25 years  `() {
        val salary = 400.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result = 10000.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val startDate = "1995-04-15"
        val endDate = "2020-04-15"
        val hint = "R3P2R5"
        assertEquals(
            result,
            calculatorFiniquitoCausalesService.getCausalRetribution(
                hint,
                salary,
                startDate,
                endDate
            )
        )
    }

    @Test
    fun `should return 25 salaries  if causal is despido intempestivo and worked 25 years and 1 month   `() {
        val salary = 400.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result = 10000.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val startDate = "1995-04-15"
        val endDate = "2020-05-15"
        val hint = "R3P2R5"
        assertEquals(
            result,
            calculatorFiniquitoCausalesService.getCausalRetribution(
                hint,
                salary,
                startDate,
                endDate
            )
        )
    }

}