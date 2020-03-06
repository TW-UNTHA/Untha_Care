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

fun isFirstDayOfMarch(calendarStartDate: Calendar) =
    calendarStartDate.get(Calendar.MONTH) == Calendar.MARCH && calendarStartDate.get(Calendar.DAY_OF_MONTH) == 1


fun lastDayOfFebruary(year: Int): Int {
    val date = Calendar.getInstance()
    date[Calendar.MONTH] = Calendar.FEBRUARY
    date[Calendar.YEAR] = year
    return date.getActualMaximum(Calendar.DAY_OF_MONTH)
}


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

fun salaryEquivalentPerDay(salary: BigDecimal) = (salary.toDouble() / DAYS_IN_MONTH).toBigDecimal()

fun startDateOfMonth(endDate: String) =
    endDate.substring(START_INDEX_YEAR, END_INDEX_MONTH).plus("01")

fun salaryForDaysWorked(
    startDate: String,
    endDate: String,
    salaryAtMonth: BigDecimal
): BigDecimal {
    val numberDaysWorked = numberDaysWorked(endDate, startDate)

    val salaryForDay = salaryEquivalentPerDay(salaryAtMonth)
    val salaryForDaysWorked = salaryForDay * numberDaysWorked.toBigDecimal()
    return salaryForDaysWorked
}

fun numberDaysWorked(endDate: String, startDate: String): Int {
    val endDateTransformed = stringToCalendar(endDate)
    val numberDays =
        calculateNumberOfDayBetween(stringToCalendar(startDate), endDateTransformed)
    val startDate = if (numberDays > DAYS_IN_MONTH) startDateOfMonth(endDate) else startDate
    val startDateTransformed = stringToCalendar(startDate)

    val numberBetween = DAYS_IN_MONTH - startDateTransformed.get(Calendar.DAY_OF_MONTH) + 1

    val numberDaysWorked =
        if (has31Days(endDateTransformed)) numberBetween else calculateNumberOfDayBetween(
            startDateTransformed,
            endDateTransformed
        ) + 1
    return numberDaysWorked
}

fun has31Days(date: Calendar): Boolean {
    return date.get(Calendar.DAY_OF_MONTH) == date.getActualMaximum((Calendar.DAY_OF_MONTH))
}

