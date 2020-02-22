package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.DAYS_31
import com.untha.utils.ConstantsCalculators.DAYS_IN_MONTH
import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.ConstantsCalculators.FIRST_DAY_MONTH
import com.untha.utils.ConstantsCalculators.MONTHS_OF_YEAR
import com.untha.utils.ConstantsCalculators.PERCENTAJE_APORTE_FONDOS_RESERVA
import com.untha.utils.ConstantsCalculators.PERCENTAJE_APORTE_IESS_PRIVADO
import com.untha.utils.ConstantsCalculators.SBU
import com.untha.utils.ConstantsCalculators.VACATIONS_ONE_YEAR
import com.untha.utils.ConstantsCalculators.WEEKLY_HOURS_COMPLETE
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class CalculatorsService {

    companion object {
        const val SIERRA_ORIENTE = 2
        const val COSTA_GALAPAGOS = 1
        const val COMPLETA = 1
        const val PARCIAL = 2
    }

    fun getDecimoTercerSueldoMensualizado(salary: BigDecimal): BigDecimal {
        val result: BigDecimal =
            salary.divide(MONTHS_OF_YEAR.toBigDecimal(), 2, RoundingMode.HALF_UP)
        return result
    }

    fun getDecimoTercerSueldoAcumulado(salary: BigDecimal, startDate: String, endDate: String):
            BigDecimal {
        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)
        val year = calendarEndDate.get(Calendar.YEAR) - 1


        when (isFirstDayOfDecember(calendarStartDate) && isLastDayOfNovember(calendarEndDate)) {
            true -> return salary.setScale(2, RoundingMode.HALF_UP)

            false ->{
                val newStartDate = setStartDateDecimoTercero(calendarEndDate, calendarStartDate)
                return calculateDecimoTerceroAcumulado(salary, newStartDate, calendarEndDate)

            }

        }
    }

    fun getDecimoCuartoSueldoMensualizado(idWorkday: Int, numberOfHoursWeekly: Int=0): BigDecimal {
        if (idWorkday == COMPLETA) {
            return SBU.toBigDecimal().divide(MONTHS_OF_YEAR.toBigDecimal(), 2, RoundingMode.HALF_UP)
        }
        val equivalentSalary = calculateSalaryEquivalent(numberOfHoursWeekly)
        return equivalentSalary.divide(MONTHS_OF_YEAR.toBigDecimal(), 2, RoundingMode.HALF_UP)
    }

    fun getDecimoCuartoAcumulado(
        startDate: String,
        endDate: String,
        idWorkDay: Int,
        idArea: Int,
        numberOfHoursWeekly: Int = 0
    ): BigDecimal? {
        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)

        when (idArea) {
            SIERRA_ORIENTE -> {
                when (idWorkDay) {
                    COMPLETA -> {
                        return calculateDecimoCuartoCompleteTime(
                            calendarEndDate,
                            calendarStartDate,
                            SIERRA_ORIENTE
                        )

                    }
                    PARCIAL -> {
                        return calculateDecimoCuartoPartialTime(
                            calendarEndDate,
                            numberOfHoursWeekly,
                            calendarStartDate,
                            SIERRA_ORIENTE
                        )
                    }
                }
            }
            COSTA_GALAPAGOS -> {
                when (idWorkDay) {
                    COMPLETA -> {
                        return calculateDecimoCuartoCompleteTime(
                            calendarEndDate,
                            calendarStartDate,
                            COSTA_GALAPAGOS
                        )
                    }
                    PARCIAL -> {
                        return calculateDecimoCuartoPartialTime(
                            calendarEndDate,
                            numberOfHoursWeekly,
                            calendarStartDate,
                            COSTA_GALAPAGOS
                        )
                    }
                }
            }

        }
        return null
    }

    fun getAportacionMensualIESS(salary: BigDecimal): BigDecimal? {
        val result = salary.multiply(PERCENTAJE_APORTE_IESS_PRIVADO.toBigDecimal())
        return result.setScale(2, RoundingMode.HALF_UP)
    }

    fun getFondoReservaMensualizado(
        startDate: String,
        endDate: String,
        salary: BigDecimal
    ): BigDecimal? {
        val numberOfDays =
            calculateDaysBetween(stringToCalendar(startDate), stringToCalendar(endDate))
        if (numberOfDays > DAYS_OF_YEAR) {
            val result = salary.multiply(PERCENTAJE_APORTE_FONDOS_RESERVA.toBigDecimal())
            return result.setScale(2, RoundingMode.HALF_UP)
        } else {
            return 0.00.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        }
    }

    fun getVacations(startDate: String, endDate: String): BigDecimal {
        val numberOfDays =
            calculateDaysBetween(stringToCalendar(startDate), stringToCalendar(endDate))
        val equivalentDay = VACATIONS_ONE_YEAR.toBigDecimal().multiply(numberOfDays.toBigDecimal())

        return equivalentDay.divide(DAYS_OF_YEAR.toBigDecimal(), 2, RoundingMode.HALF_UP)
    }

    private fun calculateDecimoCuartoPartialTime(
        calendarEndDate: Calendar,
        numberOfHoursWeekly: Int,
        calendarStartDate: Calendar,
        area: Int
    ): BigDecimal? {

        var startDate: Calendar = Calendar.getInstance()

        if (area == SIERRA_ORIENTE) {
            startDate = setStartDate(calendarEndDate, calendarStartDate, area)
        } else
            if (area == COSTA_GALAPAGOS) {
                if (isFirstDayOfMarch(calendarStartDate) && isLastDayOfFebruary(calendarEndDate)) {
                    val salary = calculateSalaryEquivalent(numberOfHoursWeekly)
                    return formulaDecimoCuarto(
                        salary,
                        DAYS_OF_YEAR.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                    )
                }
                startDate = setStartDate(calendarEndDate, calendarStartDate, area)
            }
        return calculateDecimoCuarto(
            startDate,
            calendarEndDate,
            numberOfHoursWeekly
        )
    }

    private fun calculateDecimoCuartoCompleteTime(
        calendarEndDate: Calendar,
        calendarStartDate: Calendar,
        area: Int
    ): BigDecimal? {
        var startDate: Calendar = Calendar.getInstance()

        if (area == SIERRA_ORIENTE) {
            startDate = setStartDate(calendarEndDate, calendarStartDate, area)
        } else
            if (area == COSTA_GALAPAGOS) {
                if (isFirstDayOfMarch(calendarStartDate) && isLastDayOfFebruary(calendarEndDate)) {
                    return SBU.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                }
                startDate = setStartDate(calendarEndDate, calendarStartDate, area)
            }

        return calculateDecimoCuarto(
            startDate,
            calendarEndDate
        )
    }

    private fun setStartDate(
        calendarEndDate: Calendar,
        calendarStartDate: Calendar,
        idArea: Int
    ): Calendar {
        val yearCurrent = calendarEndDate.get(Calendar.YEAR)
        val oneYearBefore = calendarEndDate.get(Calendar.YEAR) - 1
        val endDateSierra = "-07-31"
        val startDateSierra = "-08-01"
        val startDateCosta = "-03-01"
        val endDateCosta = "-02-${getLastDayOfFebruary(yearCurrent)}"
        var startDate: Calendar

        var posfixStartDate: String
        var posfixEndDate: String
        if (idArea == SIERRA_ORIENTE) {
            posfixStartDate = startDateSierra
            posfixEndDate = endDateSierra
        } else {
            posfixStartDate = startDateCosta
            posfixEndDate = endDateCosta

        }
        if (calendarEndDate.after(stringToCalendar("$yearCurrent${posfixEndDate}"))) {
            if (calendarStartDate.after(stringToCalendar("${yearCurrent}${posfixStartDate}"))) {
                startDate = calendarStartDate
            } else {
                startDate = stringToCalendar("$yearCurrent${posfixStartDate}")
            }
        } else {
            if (calendarStartDate.after(stringToCalendar("$oneYearBefore${posfixStartDate}"))) {
                startDate = calendarStartDate
            } else {
                startDate = stringToCalendar("$oneYearBefore${posfixStartDate}")
            }
        }
        return startDate
    }


    private fun calculateDecimoCuarto(
        calendarStartDate: Calendar,
        calendarEndDate: Calendar,
        numberOfHoursWeekly: Int = 0
    ): BigDecimal? {

        val daysWorked: BigDecimal = calculateDaysBetween(
            calendarStartDate,
            calendarEndDate
        ).toBigDecimal()

        when (numberOfHoursWeekly) {
            0 -> return formulaDecimoCuarto(SBU.toBigDecimal(), daysWorked)
            else -> {
                val salary = calculateSalaryEquivalent(numberOfHoursWeekly)
                return formulaDecimoCuarto(salary, daysWorked)
            }
        }
    }

    private fun calculateSalaryEquivalent(numberOfHoursWeekly: Int): BigDecimal {
        val equivalent =
            numberOfHoursWeekly.toBigDecimal().divide(WEEKLY_HOURS_COMPLETE.toBigDecimal())
                .multiply(SBU.toBigDecimal())
        return equivalent.setScale(2, RoundingMode.HALF_UP)
    }

    private fun formulaDecimoCuarto(
        basicSalary: BigDecimal,
        numberOfDays: BigDecimal
    ): BigDecimal? {
        val multiplier = basicSalary.multiply(numberOfDays)
        return multiplier.divide(
            DAYS_OF_YEAR.toBigDecimal(),
            2,
            RoundingMode.HALF_UP
        )
    }


    fun stringToCalendar(date: String): Calendar {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyy-MM-dd", Locale.getDefault())
        calendar.time = sdf.parse(date)
        return calendar
    }

    private fun calculateDaysBetween(
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


    private fun calculateDecimoTerceroAcumulado(
        salary: BigDecimal,
        startDate: Calendar,
        endDate: Calendar
    ): BigDecimal {
        var newStarDate = startDate
        var numberOfDays = calculateDaysBetween(newStarDate, endDate)
        when (numberOfDays > DAYS_OF_YEAR) {
            true -> {
                val yearInitial = endDate.get(Calendar.YEAR)
                newStarDate = stringToCalendar("$yearInitial-12-01")
                numberOfDays = calculateDaysBetween(newStarDate, endDate)
            }
        }
        return formulaDecimoTerceroAcumulado(salary, numberOfDays).setScale(
            2,
            RoundingMode.HALF_UP
        )
    }

    private fun formulaDecimoTerceroAcumulado(
        salary: BigDecimal,
        numberOfDays: Int
    ): BigDecimal {
        return salary.multiply(numberOfDays.toBigDecimal())
            .divide(DAYS_OF_YEAR.toBigDecimal(), 2, RoundingMode.HALF_UP)
    }

    private fun isFirstDayOfDecember(startDate: Calendar): Boolean {
        return startDate.get(Calendar.MONTH) == Calendar.DECEMBER &&
                startDate.get(Calendar.DAY_OF_MONTH) == FIRST_DAY_MONTH
    }

    private fun isLastDayOfNovember(endDate: Calendar): Boolean {
        return endDate.get(Calendar.MONTH) == Calendar.NOVEMBER &&
                endDate.get(Calendar.DAY_OF_MONTH) == DAYS_IN_MONTH

    }

    private fun isLastDayOfFebruary(date: Calendar): Boolean {
        return date.get(Calendar.MONTH) == Calendar.FEBRUARY &&
                date.get(Calendar.DAY_OF_MONTH) == date.getActualMaximum((Calendar.DAY_OF_MONTH))
    }


    private fun getLastDayOfFebruary(year: Int): Int {
        val dateFebruary = Calendar.getInstance()
        dateFebruary[Calendar.MONTH] = Calendar.FEBRUARY
        dateFebruary[Calendar.YEAR] = year
        return dateFebruary.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun isFirstDayOfMarch(calendarStartDate: Calendar) =
        calendarStartDate.get(Calendar.MONTH) == Calendar.MARCH && calendarStartDate.get(Calendar.DAY_OF_MONTH) == 1

    private fun setStartDateDecimoTercero(
        calendarEndDate: Calendar,
        calendarStartDate: Calendar
    ): Calendar {
        val yearCurrent = calendarEndDate.get(Calendar.YEAR)
        val oneYearBefore = calendarEndDate.get(Calendar.YEAR) - 1
        val posfixEndDate = "-11-30"
        val posfixStartDate = "-12-01"
        var startDate: Calendar


        if (calendarEndDate.after(stringToCalendar("$yearCurrent${posfixEndDate}"))) {
            if (calendarStartDate.after(stringToCalendar("${yearCurrent}${posfixStartDate}"))) {
                startDate = calendarStartDate
            } else {
                startDate = stringToCalendar("$yearCurrent${posfixStartDate}")
            }
        } else {
            if (calendarStartDate.after(stringToCalendar("$oneYearBefore${posfixStartDate}"))) {
                startDate = calendarStartDate
            } else {
                startDate = stringToCalendar("$oneYearBefore${posfixStartDate}")
            }
        }
        return startDate
    }
}

