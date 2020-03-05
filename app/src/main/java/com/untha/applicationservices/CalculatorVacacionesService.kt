package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.ConstantsCalculators.MONTHS_OF_YEAR
import com.untha.utils.calculateNumberOfDayBetween
import com.untha.utils.equivalentOfAnnualLeavesToDay
import com.untha.utils.getAge
import com.untha.utils.newStartDatePeriodOfWork
import com.untha.utils.numberOfAnnualLeavesPerAge
import com.untha.utils.stringToCalendar
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class CalculatorVacacionesService {

    companion object {
        const val SIERRA_ORIENTE = 2
        const val COSTA_GALAPAGOS = 1
        const val COMPLETE = 1
        const val PARTIAL = 2
        const val EIGHTEEN_AGE = 18
        const val SEVENTEEN_AGE = 17
        const val FIFTEEN_AGE = 15
        const val SIXTEEN_AGE = 16
        const val TWENTY_AGE = 20
        const val START_INDEX = 4
        const val END_INDEX = 10
        const val START_INDEX_YEAR = 0
        const val END_INDEX_MONTH = 8
    }


    fun getNumberAnnualLeaveDay(startDate: String, endDate: String, age: Int): Double {
        val numberAnnualLeaveAtYear = numberOfAnnualLeavesPerAge(age)
        val numberAnnualLeaveAtDay = equivalentOfAnnualLeavesToDay(numberAnnualLeaveAtYear)
        val startDatePeriodOfWork = newStartDatePeriodOfWork(startDate, endDate)
        val numberDayWorked =
            calculateNumberOfDayBetween(
                stringToCalendar(startDatePeriodOfWork),
                stringToCalendar(endDate)
            )
        return numberDayWorked * numberAnnualLeaveAtDay
    }


    fun getCostOfAnnualLeaveDays(
        startDate: String,
        endDate: String,
        salary: Double,
        age: Int,
        daysTaken: Int
    ): BigDecimal {
        val numberAnnualLeaveDays = getNumberAnnualLeaveDay(startDate, endDate, age)
        val numberAnnualLeaveDaysAvailable = numberAnnualLeaveDays - daysTaken
        val salaryAtDay = getDailyPayAnnualLeave(salary)

        val result = salaryAtDay.multiply(
            numberAnnualLeaveDaysAvailable.toBigDecimal().setScale(
                2,
                RoundingMode.HALF_UP
            )
        )
        return result
    }


    fun isBirthdayInPeriodOfWork(startDate: String, endDate: String, birthDate: String): Boolean {
        val ageStartDate = getAge(birthDate, startDate)
        val ageEndDate = getAge(birthDate, endDate)
        if (ageStartDate < ageEndDate) {
            return true
        }
        return false
    }

    fun getNewDate(startDate: String, endDate: String, birthDate: String): String {
        val startDateTransformed = stringToCalendar(startDate)
        val endDateTransformed = stringToCalendar(endDate)
        val birthDateTransformed = stringToCalendar(birthDate)
        val newBirthdayStartDate = startDateTransformed.get(Calendar.YEAR).toString()
            .plus(birthDate.substring(START_INDEX, END_INDEX))
        val newBirthdayEndDate = endDateTransformed.get(Calendar.YEAR).toString()
            .plus(birthDate.substring(START_INDEX, END_INDEX))

        val isEqualStartDateWithBirthday = newBirthdayStartDate.equals(birthDateTransformed)
        val isEqualEndDateWithBirthday = endDateTransformed.equals(birthDateTransformed)
        if (isEqualStartDateWithBirthday || isEqualEndDateWithBirthday) {
            return endDate
        }
        if (isBirthdayInPeriodOfWork(startDate, endDate, birthDate)) {
            val newBirthdayTransformed = stringToCalendar(newBirthdayStartDate)
            if (newBirthdayTransformed.after(startDateTransformed)) {
                return newBirthdayStartDate
            }
            return newBirthdayEndDate
        }
        return endDate
    }

    fun getTotalCostFirstPeriod(
        startDate: String,
        endDate: String,
        birthDate: String,
        salary: Double,
        daysTaken: Int
    ): BigDecimal {
        val endDateByPeriod = getNewDate(startDate, endDate, birthDate)

        val age = getAge(birthDate, startDate)
        val cost =
            getCostOfAnnualLeaveDays(startDate, endDateByPeriod, salary, age, daysTaken)


        return cost.setScale(2, RoundingMode.HALF_UP)
    }

    fun getTotalCostSecondPeriod(
        startDate: String,
        endDate: String,
        birthDate: String,
        salary: Double,
        daysTaken: Int
    ): BigDecimal {
        val startDateByPeriod = getNewDate(startDate, endDate, birthDate)
        val age = getAge(birthDate, startDateByPeriod)

        val cost =
            getCostOfAnnualLeaveDays(startDateByPeriod, endDate, salary, age, daysTaken)

        return cost.setScale(2, RoundingMode.HALF_UP)
    }

    fun getTotalCostPeriod(
        startDate: String,
        endDate: String,
        birthDate: String,
        salary: Double,
        daysTaken: Int
    ): BigDecimal {
        var cost: BigDecimal
        val newDate = getNewDate(startDate, endDate, birthDate)
        cost = getTotalCostFirstPeriod(startDate, endDate, birthDate, salary, daysTaken)
            .setScale(2, RoundingMode.HALF_UP)
        if (newDate.equals(startDate) || newDate.equals(endDate)) {
            return cost
        }
        cost += getTotalCostSecondPeriod(startDate, endDate, birthDate, salary, daysTaken)

        return cost.setScale(2, RoundingMode.HALF_UP)
    }


    fun getDailyPayAnnualLeave(salary: Double): BigDecimal {
        val salaryAtDay = (salary * MONTHS_OF_YEAR) / DAYS_OF_YEAR
        return salaryAtDay.toBigDecimal()
    }

}
