package com.untha.applicationservices

import com.untha.automation.EntriesAnnualLeaveTest
import com.untha.utils.equivalentOfAnnualLeavesToDay
import com.untha.utils.getAge
import com.untha.utils.newStartDatePeriodOfWork
import com.untha.utils.numberOfAnnualLeavesPerAge
import junit.framework.Assert.assertEquals
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@RunWith(JUnitParamsRunner::class)
class CalculatorVacacionesServiceTest {
    val calculatorVacacionesService = CalculatorVacacionesService()

    @Test
    fun `should return annual leave days 15 when user is greater or equal 18 years of age `() {
        val expectedNumberOfDays = 15
        val years = 19

        val result = numberOfAnnualLeavesPerAge(years)

        assertEquals(expectedNumberOfDays, result)
    }

    @Test
    fun `should return annual leave days 20 when user is under the age of 16`() {
        val expectedNumberOfDays = 20
        val years = 15

        val result = numberOfAnnualLeavesPerAge(years)

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

        val result = numberOfAnnualLeavesPerAge(age)

        assertEquals(expectedNumberOfDays, result)
    }


    @Test
    fun `should return annual leave value equivalent to one day when user is under the age of 16`() {
        val numberOfDays = 20
        val expectedPercentageDay = numberOfDays.toDouble().div(360.toDouble())

        val result = equivalentOfAnnualLeavesToDay(numberOfDays)

        assertEquals(expectedPercentageDay, result)
    }

    @Test
    fun `should return annual leave value equivalent to one day when user is between 16 and 18 years old`() {
        val numberOfDays = 18
        val expectedPercentageDay = numberOfDays.toDouble().div(360.toDouble())

        val result = equivalentOfAnnualLeavesToDay(numberOfDays)

        assertEquals(expectedPercentageDay, result)
    }

    @Test
    fun `should return annual leave value equivalent to one day when user is over the age of 18`() {
        val numberOfDays = 15
        val expectedPercentageDay = numberOfDays.toDouble().div(360.toDouble())

        val result = equivalentOfAnnualLeavesToDay(numberOfDays)

        assertEquals(expectedPercentageDay, result)
    }

    @Test
    fun `should return annual leave days 18,0 per year when user is 16 age and has one year`() {
        val age = 16
        val startDate = "2019-08-01"
        val endDate = "2020-08-01"
        val expectedNumberAnnualLeaveDays = 18.0

        val result = calculatorVacacionesService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }

    @Test
    fun `should return annual leave days 15,0 per year when user is 19 age and has one year`() {
        val age = 19
        val startDate = "2019-08-01"
        val endDate = "2020-08-01"
        val expectedNumberAnnualLeaveDays = 15.0

        val result = calculatorVacacionesService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }

    @Test
    fun `should return annual leave days 5,0 per year when user is 19 age and has 4 months`() {
        val age = 19
        val startDate = "2019-08-01"
        val endDate = "2019-12-01"
        val expectedNumberAnnualLeaveDays = 5.0

        val result = calculatorVacacionesService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }

    @Test
    fun `should return annual leave days 5,58 per year when user is 19 age and has 4 months`() {
        val age = 19
        val startDate = "2019-08-01"
        val endDate = "2019-12-15"
        val expectedNumberAnnualLeaveDays = 5.583333333333333

        val result = calculatorVacacionesService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }

    @Test
    fun `should return annual leave days 7 per year when user is 19 age and has 4 months`() {
        val age = 19
        val startDate = "2019-01-01"
        val endDate = "2019-12-31"
        val expectedNumberAnnualLeaveDays = 15.0

        val result = calculatorVacacionesService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }


    @Test
    fun `should return the new start date 2019-02-01 with last year when number of days is over 360 days`() {
        val startDate = "2018-02-01"
        val endDate = "2019-08-31"
        val expectedStartDate = "2019-02-01"

        val result = newStartDatePeriodOfWork(startDate, endDate)

        assertEquals(expectedStartDate, result)
    }

    @Test
    fun `should return the new start date with last year when number of days is over 720 days`() {
        val startDate = "2017-02-01"
        val endDate = "2019-08-31"
        val expectedStartDate = "2019-02-01"

        val result = newStartDatePeriodOfWork(startDate, endDate)

        assertEquals(expectedStartDate, result)
    }

    @Test
    fun `should return the new start date with last year when number of days is over 3 years`() {
        val startDate = "2010-02-01"
        val endDate = "2015-08-31"
        val expectedStartDate = "2015-02-01"

        val result = newStartDatePeriodOfWork(startDate, endDate)

        assertEquals(expectedStartDate, result)
    }

    @Test
    fun `should return annual leave days 15,0 per year when user is 19 age and has 4 months`() {
        val age = 19
        val startDate = "2018-02-01"
        val endDate = "2019-08-31"
        val expectedNumberAnnualLeaveDays = 8.75

        val result = calculatorVacacionesService.getNumberAnnualLeaveDay(startDate, endDate, age)

        assertEquals(expectedNumberAnnualLeaveDays, result)
    }

    fun getTestCases(): ArrayList<EntriesAnnualLeaveTest> {
        var annualLeaveTestCases: ArrayList<EntriesAnnualLeaveTest> = arrayListOf()
        annualLeaveTestCases.add(EntriesAnnualLeaveTest("2018-02-01", "2019-08-31", 19, 8.75))
        annualLeaveTestCases.add(EntriesAnnualLeaveTest("2018-02-01", "2019-08-31", 18, 8.75))
        annualLeaveTestCases.add(EntriesAnnualLeaveTest("2018-02-01", "2019-08-31", 17, 10.5))
        annualLeaveTestCases.add(EntriesAnnualLeaveTest("2018-02-01", "2019-08-31", 16, 10.5))
        annualLeaveTestCases.add(EntriesAnnualLeaveTest("2018-02-01", "2019-08-31", 15, 11.67))
        annualLeaveTestCases.add(
            EntriesAnnualLeaveTest(
                "2018-02-01",
                "2018-02-15", 15,
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
        val result = calculatorVacacionesService.getNumberAnnualLeaveDay(
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

        val result = calculatorVacacionesService.getDailyPayAnnualLeave(salary.toDouble())

        assertEquals(
            expectedEquivalentSalaryPerDay.setScale(2, RoundingMode.HALF_UP),
            result.setScale(2, RoundingMode.HALF_UP)
        )
    }

    @Test
    fun `should return equivalent salary per day when salary is 500`() {
        val salary = 500

        val expectedEquivalentSalaryPerDay = BigDecimal.valueOf(16.6666666667)

        val result = calculatorVacacionesService.getDailyPayAnnualLeave(salary.toDouble())

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
            calculatorVacacionesService.getCostOfAnnualLeaveDays(
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
            calculatorVacacionesService.getCostOfAnnualLeaveDays(
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

        val result = getAge(birthDate)

        assertEquals(expectedAge, result)
    }

    @Test
    fun `should return age 17`() {
        val birthDate = "2002-02-01"
        val startDate = "2019-10-01"
        val expectedAge = 17

        val result = getAge(birthDate, startDate)

        assertEquals(expectedAge, result)
    }

    @Test
    fun `should return age 18`() {
        val birthDate = "2002-02-01"
        val endDate = "2020-08-31"
        val expectedAge = 18

        val result = getAge(birthDate, endDate)

        assertEquals(expectedAge, result)
    }

    @Test
    fun `should return true if birthday is on`() {
        val birthDate = "2002-02-01"
        val startDate = "2019-10-01"
        val endDate = "2020-08-31"

        val result =
            calculatorVacacionesService.isBirthdayInPeriodOfWork(startDate, endDate, birthDate)

        assert(result)
    }

    @Test
    fun `should return false if birthday is not on range`() {
        val birthDate = "2002-02-27"
        val startDate = "2020-03-01"
        val endDate = "2020-10-01"

        val result =
            calculatorVacacionesService.isBirthdayInPeriodOfWork(startDate, endDate, birthDate)

        assertFalse(result)
    }

    @Test
    fun `should return false if birthday not change in period of work`() {
        val birthDate = "2002-03-01"
        val startDate = "2020-03-01"
        val endDate = "2020-10-01"

        val result =
            calculatorVacacionesService.isBirthdayInPeriodOfWork(startDate, endDate, birthDate)

        assertFalse(result)
    }

    @Test
    fun `should return true if birth date is in end date period of work  `() {
        val birthDate = "2002-10-01"
        val startDate = "2020-03-01"
        val endDate = "2020-10-01"

        val result =
            calculatorVacacionesService.isBirthdayInPeriodOfWork(startDate, endDate, birthDate)

        assert(result)
    }

    @Test
    fun `should return birthday like new end date period of work when birthday is the same year of end date`() {
        val birthDate = "2002-02-01"
        val startDate = "2019-10-01"
        val endDate = "2020-10-01"
        val expectedEndDate = "2020-02-01"

        val result = calculatorVacacionesService.getNewDate(startDate, endDate, birthDate)

        assertEquals(expectedEndDate, result)
    }

    @Test
    fun `should return birthday like new end date period of work when birthday is in the same year of start date`() {
        val birthDate = "2002-12-01"
        val startDate = "2019-10-01"
        val endDate = "2020-10-01"
        val expectedEndDate = "2019-12-01"

        val result = calculatorVacacionesService.getNewDate(startDate, endDate, birthDate)

        assertEquals(expectedEndDate, result)
    }

    @Test
    fun `should return birthday like new end date period of work when birthday is one day after start date`() {
        val birthDate = "2002-10-02"
        val startDate = "2019-10-01"
        val endDate = "2020-10-01"
        val expectedEndDate = "2019-10-02"

        val result = calculatorVacacionesService.getNewDate(startDate, endDate, birthDate)

        assertEquals(expectedEndDate, result)
    }

    @Test
    fun `should return the same start date period of work when birthday is equals to startDate`() {
        val birthDate = "2002-10-01"
        val startDate = "2019-10-01"
        val endDate = "2020-10-01"
        val expectedEndDate = "2020-10-01"

        val result = calculatorVacacionesService.getNewDate(startDate, endDate, birthDate)

        assertEquals(expectedEndDate, result)
    }

    @Test
    fun `should return the same end date period of work when birthday is equals to endDate`() {
        val birthDate = "2002-10-01"
        val startDate = "2019-11-01"
        val endDate = "2020-10-01"
        val expectedEndDate = "2020-10-01"

        val result = calculatorVacacionesService.getNewDate(startDate, endDate, birthDate)

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

        val result = calculatorVacacionesService.getTotalCostFirstPeriod(
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

        val result = calculatorVacacionesService.getTotalCostFirstPeriod(
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

        val result = calculatorVacacionesService.getTotalCostFirstPeriod(
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

        val result = calculatorVacacionesService.getTotalCostFirstPeriod(
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

        val result = calculatorVacacionesService.getTotalCostFirstPeriod(
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

        val result = calculatorVacacionesService.getTotalCostSecondPeriod(
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

        val result = calculatorVacacionesService.getTotalCostPeriod(
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

        val result = calculatorVacacionesService.getTotalCostPeriod(
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

        val result = calculatorVacacionesService.getTotalCostPeriod(
            startDate,
            endDate,
            birthDate,
            salary,
            daysTaken
        )

        assertEquals(expectedCostByPeriod, result)
    }
}
