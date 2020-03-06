package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.ConstantsCalculators.MONTHS_OF_YEAR
import com.untha.utils.calculateNumberOfDayBetween
import com.untha.utils.equivalentOfAnnualLeavesToDay
import com.untha.utils.numberOfAnnualLeavesPerAge
import com.untha.utils.stringToCalendar
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class CalculatorVacacionesService {

    companion object {

        const val START_INDEX = 4
        const val END_INDEX = 10
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

    fun newStartDatePeriodOfWork(startDate: String, endDate: String): String {
        val startDateTransform = stringToCalendar(startDate)
        val endDateTransform = stringToCalendar(endDate)
        val numberDayWorked = calculateNumberOfDayBetween(startDateTransform, endDateTransform)
        val difference = numberDayWorked / DAYS_OF_YEAR
        if (numberDayWorked > DAYS_OF_YEAR) {
            val newYear = startDateTransform.get(Calendar.YEAR) + difference
            val newStartDay = newYear.toString().plus(
                startDate.substring(
                    CalculatorDecimosService.START_INDEX,
                    CalculatorDecimosService.END_INDEX
                )
            )
            return newStartDay
        }
        return startDate
    }

    fun getAge(birthDate: String, date: String? = null): Int {
        val birthDateTransform = stringToCalendar(birthDate)
        val currentDay: Calendar =
            if (date.isNullOrEmpty()) Calendar.getInstance() else stringToCalendar(date)

        var age = currentDay.get(Calendar.YEAR) - birthDateTransform.get(Calendar.YEAR)
        if (currentDay.get(Calendar.DAY_OF_YEAR) < birthDateTransform.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }


}
