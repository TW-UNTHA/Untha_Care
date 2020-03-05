package com.untha.utils

import com.untha.applicationservices.CalculatorDecimosService
import com.untha.applicationservices.CalculatorDecimosService.Companion.END_INDEX_MONTH
import com.untha.applicationservices.CalculatorDecimosService.Companion.START_INDEX_YEAR
import com.untha.utils.ConstantsCalculators.DAYS_31
import com.untha.utils.ConstantsCalculators.DAYS_IN_MONTH
import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.ConstantsCalculators.FIRST_DAY_MONTH
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*


fun isFirstDayOfDecember(startDate: Calendar): Boolean {
    return startDate.get(Calendar.MONTH) == Calendar.DECEMBER &&
            startDate.get(Calendar.DAY_OF_MONTH) == FIRST_DAY_MONTH
}

fun isLastDayOfNovember(endDate: Calendar): Boolean {
    return endDate.get(Calendar.MONTH) == Calendar.NOVEMBER &&
            endDate.get(Calendar.DAY_OF_MONTH) == DAYS_IN_MONTH

}

fun isLastDayOfFebruary(date: Calendar): Boolean {
    return date.get(Calendar.MONTH) == Calendar.FEBRUARY &&
            date.get(Calendar.DAY_OF_MONTH) == date.getActualMaximum((Calendar.DAY_OF_MONTH))
}


fun lastDayOfFebruary(year: Int): Int {
    val date = Calendar.getInstance()
    date[Calendar.MONTH] = Calendar.FEBRUARY
    date[Calendar.YEAR] = year
    return date.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun isFirstDayOfMarch(calendarStartDate: Calendar) =
    calendarStartDate.get(Calendar.MONTH) == Calendar.MARCH && calendarStartDate.get(Calendar.DAY_OF_MONTH) == 1

fun calculateNumberOfDayBetween(
    calendarStartDate: Calendar,
    calendarEndDate: Calendar
): Int {

    var startDay = calendarStartDate.get(Calendar.DAY_OF_MONTH)
    val startMonth = calendarStartDate.get(Calendar.MONTH)
    val startYear = calendarStartDate.get(Calendar.YEAR)
    var endDay = calendarEndDate.get(Calendar.DAY_OF_MONTH)
    val endMonth = calendarEndDate.get(Calendar.MONTH)
    val endYear = calendarEndDate.get(Calendar.YEAR)

    if (startDay == DAYS_31 || isLastDayOfFebruary(calendarStartDate)) {
        startDay = DAYS_IN_MONTH
    }
    if (startDay == DAYS_IN_MONTH && endDay == DAYS_31) {
        endDay = DAYS_IN_MONTH
    }

    return ((endYear - startYear) * DAYS_OF_YEAR) + ((endMonth - startMonth) * DAYS_IN_MONTH) + (endDay - startDay)
}

fun stringToCalendar(date: String): Calendar {
    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyy-MM-dd", Locale.getDefault())
    calendar.time = sdf.parse(date)
    return calendar
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

fun numberOfAnnualLeavesPerAge(years: Int): Int {
    if (years >= CalculatorDecimosService.EIGHTEEN_AGE) {
        return CalculatorDecimosService.FIFTEEN_AGE
    }
    if (years in CalculatorDecimosService.SIXTEEN_AGE..CalculatorDecimosService.SEVENTEEN_AGE)
        return CalculatorDecimosService.EIGHTEEN_AGE
    return CalculatorDecimosService.TWENTY_AGE
}

fun equivalentOfAnnualLeavesToDay(numberOfDays: Int): Double {
    return numberOfDays.toDouble().div(DAYS_OF_YEAR.toDouble())
}

fun salaryEquivalentPerDay(salary: BigDecimal) = (salary.toDouble()/DAYS_IN_MONTH).toBigDecimal()

fun startDateOfMonth(endDate: String) =
    endDate.substring(START_INDEX_YEAR, END_INDEX_MONTH).plus("01")

fun salaryForDaysWorked(
    startDate: String,
    endDate: String,
    salaryAtMonth: BigDecimal
): BigDecimal {
    val endDateTransformed = stringToCalendar(endDate)
    val numberDays =
        calculateNumberOfDayBetween(stringToCalendar(startDate), endDateTransformed)
    val startDate = if (numberDays > DAYS_IN_MONTH) startDateOfMonth(endDate) else startDate

    val startDateTransformed = stringToCalendar(startDate)

    val numberOfDays = calculateNumberOfDayBetween(startDateTransformed, endDateTransformed)
    val salaryForDay = salaryEquivalentPerDay(salaryAtMonth)
    val salaryForDaysWorked = salaryForDay * numberOfDays.toBigDecimal()
    return salaryForDaysWorked
}

