package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.DAYS_31
import com.untha.utils.ConstantsCalculators.DAYS_IN_MONTH
import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.ConstantsCalculators.FIRST_DAY_MONTH
import com.untha.utils.ConstantsCalculators.MONTHS_OF_YEAR
import com.untha.utils.ConstantsCalculators.SBU
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class CalculatorsService {

    companion object {
        const val SIERRA_ORIENTE = 2
        const val COMPLETA = 1
        const val PARCIAL = 2
    }

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

    fun calculateDecimoCuartoSueldoMensuaizado(): BigDecimal {
        return SBU.toBigDecimal().divide(MONTHS_OF_YEAR.toBigDecimal(), 2, RoundingMode.HALF_UP)

    }

    fun getDecimoCuartoAcumulado(
        startDate: String,
        endDate: String,
        idWorkDay: Int,
        idArea: Int
    ): BigDecimal? {
        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)
        val yearInicial = calendarEndDate.get(Calendar.YEAR)
        val year = calendarEndDate.get(Calendar.YEAR) - 1

        when (idArea) {
            SIERRA_ORIENTE -> {
                when (idWorkDay) {
                    COMPLETA -> {
                        when (calendarStartDate.compareTo(stringToCalendar("$year-08-01")) < 0) {
                            true -> {
                                return calculateDecimoCuarto(
                                    year,
                                    calendarEndDate,
                                    yearInicial
                                )
                            }
                            else -> {
                                val daysWorked: BigDecimal = calculateDaysBetween(
                                    calendarStartDate,
                                    calendarEndDate
                                ).toBigDecimal()
                                return calculateDecimoCuartoAcumulado(daysWorked)
                            }
                        }

                    }
                    PARCIAL -> {
                        println("parcial")
                        return null

                    }
                }
            }
        }
        return null

    }

    private fun calculateDecimoCuarto(
        year: Int,
        calendarEndDate: Calendar,
        yearInicial: Int
    ): BigDecimal? {
        var newStartDate = stringToCalendar("$year-08-01")
        var daysWorked: BigDecimal = calculateDaysBetween(
            newStartDate,
            calendarEndDate
        ).toBigDecimal()
        when (calculateDaysBetween(
            newStartDate,
            calendarEndDate
        ) > DAYS_OF_YEAR) {
            true -> {
                newStartDate = stringToCalendar("$yearInicial-08-01")
                daysWorked = calculateDaysBetween(
                    newStartDate,
                    calendarEndDate
                ).toBigDecimal()


            }
        }
        return calculateDecimoCuartoAcumulado(daysWorked)
    }

    private fun calculateDecimoCuartoAcumulado(daysWorked: BigDecimal): BigDecimal? {
        val multiplier = SBU.toBigDecimal().multiply(daysWorked)
        return multiplier.divide(
            DAYS_OF_YEAR.toBigDecimal(),
            2,
            RoundingMode.HALF_UP
        )
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
        var newStarDate = startDate
        val yearInicial = endDate.get(Calendar.YEAR)
        var numberOfDays =
            calculateDaysBetween(newStarDate, endDate)
        when (numberOfDays > DAYS_OF_YEAR) {
            true -> {
                newStarDate = stringToCalendar("$yearInicial-12-01")
                numberOfDays = calculateDaysBetween(newStarDate, endDate)
            }
        }

        val decimoTercero = salary.setScale(
            2,
            RoundingMode.HALF_UP
        ) * numberOfDays.toBigDecimal() / DAYS_OF_YEAR.toBigDecimal()
        return decimoTercero.setScale(2, RoundingMode.HALF_UP)
    }

    private fun isDecember(startDate: Calendar): Boolean {
        return startDate.get(Calendar.MONTH) == Calendar.DECEMBER &&
                startDate.get(Calendar.DAY_OF_MONTH) == FIRST_DAY_MONTH
    }

    private fun isNovember(endDate: Calendar): Boolean {
        return endDate.get(Calendar.MONTH) == Calendar.NOVEMBER &&
                endDate.get(Calendar.DAY_OF_MONTH) == DAYS_IN_MONTH

    }
}

