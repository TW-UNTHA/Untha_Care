package com.untha.utils

import android.content.Context
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.untha.R
import java.math.RoundingMode
import java.util.*

const val MONTH_WITH_TWO_DIGITS = 10
const val MAX_HOURS_WEEK = 60
const val SIERRA_ORIENTE = 2
const val COSTA_GALAPAGOS = 1
const val COMPLETE_HOURS = 40
const val PARTIAL_TIME = 2
const val COMPLETE_TIME = 1

fun validationStartDate(startYear : Spinner, startMonth: Spinner, startDay: Spinner, context: Context): Boolean {
    val isInvalidDate =
        isDaySelectedMajorOfLastDayMonth(
            startYear,
            startMonth,
            startDay
        )
    if (isInvalidDate) {
        showToast(R.string.wrong_date_start, context)
        return false
    }
    return true
}
fun isDaySelectedMajorOfLastDayMonth(
    spinnerYear: Spinner,
    spinnerMonth: Spinner,
    spinnerDay: Spinner
): Boolean {
    val dateSelected = Calendar.getInstance()
    dateSelected[Calendar.MONTH] = spinnerMonth.selectedItemPosition
    dateSelected[Calendar.YEAR] = spinnerYear.selectedItem.toString().toInt()

    val isMoreThanDayLimit =
        spinnerDay.selectedItem.toString().toInt() > getLastDayOfMonth(dateSelected)
    return isMoreThanDayLimit
}

private fun getLastDayOfMonth(date: Calendar) = date.getActualMaximum((Calendar.DAY_OF_MONTH))

fun transformToCalendarDate(
    spinnerYear: Spinner,
    spinnerMonth: Spinner,
    spinnerDay: Spinner
): Calendar {
    val buildDate = buildDate(spinnerYear, spinnerMonth, spinnerDay)
    return stringToCalendar(buildDate)
}

fun buildDate(
    spinnerYear: Spinner,
    spinnerMonth: Spinner,
    spinnerDay: Spinner
): String {
    val positionIncremented = spinnerMonth.selectedItemPosition

    val transformMonth = transformationMonth(positionIncremented)
    val buildDate = spinnerYear.selectedItem.toString().plus("-")
        .plus(transformMonth).plus("-")
        .plus(addZero(spinnerDay.selectedItem.toString().toInt()))
    return buildDate
}


fun transformationMonth(month: Int): String {
    val positionIncremented = month + 1
    val addZeroToMonth = addZero(positionIncremented)

    return addZeroToMonth
}

private fun addZero(number: Int): String {
    return if (number < MONTH_WITH_TWO_DIGITS) ("0").plus(number)
    else number.toString()
}

fun validationDates(startDate: Calendar, endDate: Calendar, context: Context): Boolean {
    if (startDate.after(endDate)) {
        showToast(R.string.wrong_date_compare_dates, context)
        return false
    }
    if (startDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR) && startDate.get(Calendar.MONTH) == endDate.get(
            Calendar.MONTH
        ) && startDate.get(Calendar.DAY_OF_MONTH) == endDate.get(Calendar.DAY_OF_MONTH)
    ) {
        showToast(R.string.wrong_date_equal_dates, context)
        return false
    }
    return true
}

fun validationSalaryInput(inputSalary: EditText, context: Context): Boolean {
    if (inputSalary.text.toString().isEmpty()) {
        showToast(R.string.wrong_salary_empty, context)
        return false
    }
    val salary = inputSalary.text.toString().toBigDecimal().setScale(2, RoundingMode.HALF_UP)
    if (salary == 0.toBigDecimal().setScale(2, RoundingMode.HALF_UP)) {
        showToast(R.string.wrong_salary_zero, context)
        return false
    }
    return true
}

fun validationHours(inputHours: EditText, context: Context): Boolean {
    if (inputHours.text.toString().isEmpty()) {
        showToast(R.string.wrong_number_of_hours_minimun, context)
        return false
    }
    if (inputHours.text.toString().toInt() == 0) {
        showToast(R.string.wrong_number_of_hours_zero, context)
        return false
    }

    if (inputHours.text.toString().toInt() > MAX_HOURS_WEEK) {
        showToast(R.string.wrong_number_of_hours_maximum, context)
        return false
    }

    return true
}

fun endDateToString(endDate: Calendar): String {
    return endDate.get(Calendar.YEAR).toString().plus("-")
        .plus(transformationMonth(endDate.get(Calendar.MONTH))).plus("-")
        .plus(addZero(endDate.get(Calendar.DAY_OF_MONTH)))
}

fun getArea(spinnerArea : Spinner) = if (spinnerArea.selectedItemPosition == 0)
    SIERRA_ORIENTE else COSTA_GALAPAGOS


fun showToast(idString: Int,context : Context) {
    Toast.makeText(
        context,
        context.getString(idString),
        Toast.LENGTH_LONG
    ).show()
}
