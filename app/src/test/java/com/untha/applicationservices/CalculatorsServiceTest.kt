package com.untha.applicationservices

import junit.framework.Assert.assertEquals
import org.junit.Test


class CalculatorsServiceTest {

    @Test
    fun `should return 360  when start date 01 Jan 2020 and end date 01 Jan 2021`() {
        val calculatorsService = CalculatorsService()
        val startDate ="2020-01-01"
        val endDate = "2021-01-01"
        val expectedValue = 360

        val numberOfDays = calculatorsService.calculateDaysBetween(startDate, endDate)
        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 44 when start date 1 dic 2019 and end date 15 jan 2020 `() {
        val calculatorsService = CalculatorsService()
        val startDate ="2019-12-01"
        val endDate = "2020-01-15"
        val expectedValue = 44

        val numberOfDays = calculatorsService.calculateDaysBetween(startDate, endDate)
        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 163 when start date 1 dic 2019 and end date 14 may 2020 `() {
        val calculatorsService = CalculatorsService()
        val startDate ="2019-12-01"
        val endDate = "2020-05-14"
        val expectedValue = 163

        val numberOfDays = calculatorsService.calculateDaysBetween(startDate, endDate)
        assertEquals(expectedValue, numberOfDays)
    }


    @Test
    fun `should return 329 when start date 1 jan 2020 and end date 30 nov 2020 `() {
        val calculatorsService = CalculatorsService()
        val startDate ="2020-01-01"
        val endDate = "2020-11-30"
        val expectedValue = 329

        val numberOfDays = calculatorsService.calculateDaysBetween(startDate, endDate)
        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 160 when start date 21 feb 2020 and end date 31 jul 2020 `() {
        val calculatorsService = CalculatorsService()
        val startDate ="2020-02-21"
        val endDate = "2020-07-31"
        val expectedValue = 160

        val numberOfDays = calculatorsService.calculateDaysBetween(startDate, endDate)
        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 133 when start date 16 oct 2019 and end date 29 feb 2020 `() {
        val calculatorsService = CalculatorsService()
        val startDate ="2019-10-16"
        val endDate = "2020-02-29"
        val expectedValue = 133

        val numberOfDays = calculatorsService.calculateDaysBetween(startDate, endDate)
        assertEquals(expectedValue, numberOfDays)
    }

    @Test
    fun `should return 132 when start date 16 oct 2020 and end date 28 feb 2021 `() {
        val calculatorsService = CalculatorsService()
        val startDate ="2020-10-16"
        val endDate = "2021-02-28"
        val expectedValue = 132

        val numberOfDays = calculatorsService.calculateDaysBetween(startDate, endDate)
        assertEquals(expectedValue, numberOfDays)
    }
}