package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.ConstantsCalculators.MONTHS_OF_YEAR
import com.untha.utils.calculateNumberOfDayBetween
import com.untha.utils.equivalentOfAnnualLeavesToDay
import com.untha.utils.getAge
import com.untha.utils.numberOfAnnualLeavesPerAge
import com.untha.utils.stringToCalendar
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class CalculatorVacacionesService {

    companion object {

        const val START_INDEX = 4
        const val END_INDEX = 10
        const val FIFTEEN_DAYS = 15
        const val EIGHTEEN_YEARS = 18

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

    fun getTotalCostPeriod(
        startDate: String,
        endDate: String,
        birthDate: String,
        salary: Double,
        daysTaken: Int
    ): BigDecimal {

        val calendarEndDate = stringToCalendar(endDate)
        var newStartDate: String
        val numberOfDays = calculateNumberOfDayBetween(stringToCalendar(startDate), calendarEndDate)
        val equivalentSalary = salary * MONTHS_OF_YEAR / DAYS_OF_YEAR

        if (numberOfDays > DAYS_OF_YEAR) {
            newStartDate = (calendarEndDate.get(Calendar.YEAR) - 1).toString()
                .plus(endDate.substring(START_INDEX, END_INDEX))
        } else {
            newStartDate = startDate

        }
        val ageFirstPeriod = getAge(birthDate, newStartDate)
        if (ageFirstPeriod > EIGHTEEN_YEARS) {
            if (numberOfDays > DAYS_OF_YEAR) {
                val daysAvailable = (FIFTEEN_DAYS - daysTaken).toDouble()
                return (daysAvailable * equivalentSalary).toBigDecimal()
                    .setScale(2, RoundingMode.HALF_UP)

            } else {
                val constant = FIFTEEN_DAYS.toDouble() / DAYS_OF_YEAR.toDouble()
                val daysAvailable = (numberOfDays.toDouble() * constant) - daysTaken.toDouble()
                return (daysAvailable * equivalentSalary).toBigDecimal()
                    .setScale(2, RoundingMode.HALF_UP)
            }
        } else {

            val newDate = getNewDate(newStartDate, endDate, birthDate)
            var numberAnnualLeaveDays =
                getNumberAnnualLeaveDay(newStartDate, newDate, ageFirstPeriod)

            val ageSecondPeriod = getAge(birthDate, endDate)
            numberAnnualLeaveDays += getNumberAnnualLeaveDay(newDate, endDate, ageSecondPeriod)
            val daysAvailable = (numberAnnualLeaveDays - daysTaken).toDouble()
            return (daysAvailable * equivalentSalary).toBigDecimal()
                .setScale(2, RoundingMode.HALF_UP)

        }
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


}
