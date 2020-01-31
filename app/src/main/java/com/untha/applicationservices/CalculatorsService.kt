package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.DAYS_31
import com.untha.utils.ConstantsCalculators.DAYS_IN_MONTH
import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.ConstantsCalculators.FIRST_DAY_MONTH
import com.untha.utils.ConstantsCalculators.MONTHS_OF_YEAR
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class CalculatorsService {

    fun calculateDecimoTercerSueldoMensualizado(salary: BigDecimal): BigDecimal {
        val result: BigDecimal =
            salary.divide(MONTHS_OF_YEAR.toBigDecimal(), 2, RoundingMode.HALF_UP)
        return result

    }

    fun getDecimoTercerSueldoAcumulado(salary: BigDecimal, startDate: String, endDate: String):
            BigDecimal {

        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)
        val year = calendarEndDate.get(Calendar.YEAR) - 1


        when (isDecember(calendarStartDate) && isNovember(calendarEndDate)) {
            true -> return salary.setScale(2, RoundingMode.HALF_UP)

            false -> when (calendarStartDate.compareTo(stringToCalendar("$year-12-01")) < 0) {
                true -> {
                    val newStartDate = stringToCalendar("$year-12-01")
                    return calculateDecimoTerceroAcumulado(salary, newStartDate, calendarEndDate)
                }

                false -> {
                    return calculateDecimoTerceroAcumulado(
                        salary,
                        calendarStartDate,
                        calendarEndDate
                    )

                }
            }

        }
    }

    private fun isDecember(startDate: Calendar): Boolean {
        return startDate.get(Calendar.MONTH) == Calendar.DECEMBER &&
                startDate.get(Calendar.DAY_OF_MONTH) == FIRST_DAY_MONTH
    }

    private fun isNovember(endDate: Calendar): Boolean {
        return endDate.get(Calendar.MONTH) == Calendar.NOVEMBER &&
                endDate.get(Calendar.DAY_OF_MONTH) == DAYS_IN_MONTH

    }

    private fun stringToCalendar(date: String): Calendar {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyy-MM-dd", Locale.getDefault())
        calendar.time = sdf.parse(date)
        return calendar

    }

    private fun calculateDaysBetween(calendarStartDate: Calendar, calendarEndDate: Calendar): Int {

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

    private fun isLastDayOfFebruary(date: Calendar): Boolean {
        return date.get(Calendar.MONTH) == 1 &&
                date.get(Calendar.DAY_OF_MONTH) == date.getActualMaximum((Calendar.DAY_OF_MONTH))
    }

    private fun calculateDecimoTerceroAcumulado(
        salary: BigDecimal,
        startDate: Calendar,
        endDate: Calendar
    ): BigDecimal {
        val numberOfDays =
            calculateDaysBetween(startDate, endDate).toBigDecimal()
        val decimoTercero = salary.setScale(
            2,
            RoundingMode.HALF_UP
        ) * numberOfDays / DAYS_OF_YEAR.toBigDecimal()
        return decimoTercero.setScale(2, RoundingMode.HALF_UP)
    }

}

