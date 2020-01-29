package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.DAYS_31
import com.untha.utils.ConstantsCalculators.DAYS_IN_MONTH
import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import java.text.SimpleDateFormat
import java.util.*

class CalculatorsService {


    fun calculateDaysBetween(startDate: String, endDate: String): Int {
        val calendarStartDate = Calendar.getInstance()
        val calendarEndDate = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyy-MM-dd", Locale.getDefault())


        calendarStartDate.time = sdf.parse(startDate)
        calendarEndDate.time = sdf.parse(endDate)

        var startDay = calendarStartDate.get(Calendar.DAY_OF_MONTH)
        val startMonth = calendarStartDate.get(Calendar.MONTH)
        val startYear = calendarStartDate.get(Calendar.YEAR)
        var endDay = calendarEndDate.get(Calendar.DAY_OF_MONTH)
        val endMonth = calendarEndDate.get(Calendar.MONTH)
        val endYear = calendarEndDate.get(Calendar.YEAR)

//        println(calendarStartDate.get(Calendar.DAY_OF_MONTH))
//        println(calendarStartDate.getActualMaximum((Calendar.DAY_OF_MONTH)))

        if (startDay == DAYS_31 || isLastDayOfFebruary(calendarStartDate)) {
            startDay = DAYS_IN_MONTH
        }
        if (startDay == DAYS_IN_MONTH && endDay == DAYS_31) {
            endDay = DAYS_IN_MONTH
        }
        return ((endYear - startYear) * DAYS_OF_YEAR) + ((endMonth - startMonth) * DAYS_IN_MONTH) + (endDay - startDay)

    }

    fun isLastDayOfFebruary(date: Calendar): Boolean {
        return date.get(Calendar.MONTH) == 1 &&
                date.get(Calendar.DAY_OF_MONTH) == date.getActualMaximum((Calendar.DAY_OF_MONTH))
    }

}

