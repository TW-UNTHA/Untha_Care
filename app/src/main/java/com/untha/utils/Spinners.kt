package com.untha.utils

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.untha.R
import java.util.*


const val RANGE_LAST_DAY_OF_MONTH = 32

fun loadDaysSpinner(spinner: Spinner, context: Context) {
    spinner.adapter = ArrayAdapter(
        context,
        R.layout.spinner_item,
        loadSpinnerDays()
    )
}

private fun loadSpinnerDays(): List<Int> {
    val days = mutableListOf<Int>()
    for (day in 1 until RANGE_LAST_DAY_OF_MONTH) {
        days.add(day)
    }
    return days
}

fun loadSpinnerData(spinner: Spinner, idArray: Int, context: Context) {
    ArrayAdapter.createFromResource(
        context,
        idArray,
        R.layout.spinner_item
    ).also { adapter ->
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.adapter = adapter

    }
}

fun loadYearsAdapter(spinner: Spinner, startYear: Int=0, endYear: Int, context: Context) {
    spinner.adapter = ArrayAdapter(
        context,
        R.layout.spinner_item,
        loadSpinnerYear(startYear, endYear)
    )
}
private fun loadSpinnerYear(startYear: Int, endYear : Int): List<Int> {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = mutableListOf<Int>()
    for (year in currentYear-startYear downTo (currentYear - endYear)) {
        years.add(year)
    }
    return years
}
