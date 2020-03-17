package com.untha.applicationservices

import com.untha.utils.COMPLETE_HOURS
import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.ConstantsCalculators.MONTHS_OF_YEAR
import com.untha.utils.ConstantsCalculators.SBU
import com.untha.utils.ConstantsCalculators.WEEKLY_HOURS_COMPLETE
import com.untha.utils.calculateNumberOfDayBetween
import com.untha.utils.isFirstDayOfDecember
import com.untha.utils.isFirstDayOfMarch
import com.untha.utils.isLastDayOfFebruary
import com.untha.utils.isLastDayOfNovember
import com.untha.utils.lastDayOfFebruary
import com.untha.utils.numberDaysWorked
import com.untha.utils.salaryForDaysWorked
import com.untha.utils.stringToCalendar
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class CalculatorDecimosService {

    companion object {
        const val SIERRA_ORIENTE = 2
        const val COSTA_GALAPAGOS = 1
        const val COMPLETE = 1
        const val PARTIAL = 2
        const val EIGHTEEN_AGE = 18
        const val SEVENTEEN_AGE = 17
        const val FIFTEEN_AGE = 15
        const val SIXTEEN_AGE = 16
        const val TWENTY_AGE = 20
        const val START_INDEX = 4
        const val END_INDEX = 10
        const val START_INDEX_YEAR = 0
        const val END_INDEX_MONTH = 8
        const val MONTH_WITH_TWO_DIGITS = 10
    }

    fun getDecimoTercerSueldoAcumulado(salary: BigDecimal, startDate: String, endDate: String):
            BigDecimal {
        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)

        when (isFirstDayOfDecember(calendarStartDate) && isLastDayOfNovember(calendarEndDate)) {
            true -> return salary.setScale(2, RoundingMode.HALF_UP)

            false -> {
                val newStartDate = setStartDateDecimoTercero(calendarEndDate, calendarStartDate)
                return calculateDecimoTerceroAcumulado(salary, newStartDate, calendarEndDate)
            }

        }
    }

    fun getDecimoCuartoSueldoMensualizado(
        numberOfHoursWeekly: Int = 0
    ): BigDecimal {
        val idWorkday = if(numberOfHoursWeekly>= COMPLETE_HOURS){
            COMPLETE}else{
            PARTIAL}
        if (idWorkday == COMPLETE) {
            return SBU.toBigDecimal().divide(MONTHS_OF_YEAR.toBigDecimal(), 2, RoundingMode.HALF_UP)
        }
        val equivalentSalary = calculateSalaryEquivalent(numberOfHoursWeekly)
        return equivalentSalary.divide(MONTHS_OF_YEAR.toBigDecimal(), 2, RoundingMode.HALF_UP)
    }

    fun getDecimoCuartoAcumulado(
        startDate: String,
        endDate: String,
        idArea: Int,
        numberOfHoursWeekly: Int = 0
    ): BigDecimal {
        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)
        val idWorkDay = if(numberOfHoursWeekly>= COMPLETE_HOURS){
            COMPLETE
        }else {
            PARTIAL
        }
        when (idArea) {
            SIERRA_ORIENTE -> {
                when (idWorkDay) {
                    COMPLETE -> {
                        return calculateDecimoCuartoCompleteTime(
                            calendarEndDate,
                            calendarStartDate,
                            SIERRA_ORIENTE
                        )

                    }
                    PARTIAL -> {
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
                    COMPLETE -> {
                        return calculateDecimoCuartoCompleteTime(
                            calendarEndDate,
                            calendarStartDate,
                            COSTA_GALAPAGOS
                        )
                    }
                    PARTIAL -> {
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
        return 0.toBigDecimal()
    }


    private fun calculateDecimoCuartoPartialTime(
        calendarEndDate: Calendar,
        numberOfHoursWeekly: Int,
        calendarStartDate: Calendar,
        area: Int
    ): BigDecimal {

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
        val date = calendarToString(startDate)
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
    ): BigDecimal {
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

    private fun calendarToString(date: Calendar): String {
        return date.get(Calendar.YEAR).toString().plus("-")
            .plus(transformationMonth(date.get(Calendar.MONTH))).plus("-")
            .plus(addZero(date.get(Calendar.DAY_OF_MONTH)))
    }

    private fun transformationMonth(month: Int): String {
        val positionIncremented = month + 1
        val addZeroToMonth = addZero(positionIncremented)

        return addZeroToMonth
    }

    private fun addZero(number: Int): String {
        return if (number < MONTH_WITH_TWO_DIGITS) ("0").plus(number)
        else number.toString()
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
        val endDateCosta = "-02-${lastDayOfFebruary(yearCurrent)}"
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
    ): BigDecimal {

        val daysWorked: BigDecimal = calculateNumberOfDayBetween(
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
    ): BigDecimal {
        val multiplier = basicSalary.multiply(numberOfDays)
        return multiplier.divide(
            DAYS_OF_YEAR.toBigDecimal(),
            2,
            RoundingMode.HALF_UP
        )
    }


    private fun calculateDecimoTerceroAcumulado(
        salary: BigDecimal,
        startDate: Calendar,
        endDate: Calendar
    ): BigDecimal {
        var newStarDate = startDate
        var numberOfDays = calculateNumberOfDayBetween(newStarDate, endDate)
        when (numberOfDays > DAYS_OF_YEAR) {
            true -> {
                val yearInitial = endDate.get(Calendar.YEAR)
                newStarDate = stringToCalendar("$yearInitial-12-01")
                numberOfDays = calculateNumberOfDayBetween(newStarDate, endDate)
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

    fun getDecimoTercerSueldoFiniquitoMensualizado(
        startDate: String,
        endDate: String,
        salaryAtMonth: BigDecimal
    ): BigDecimal {
        val salaryForDaysWorked =
            salaryForDaysWorked(startDate, endDate, salaryAtMonth)
        val result: BigDecimal =
            salaryForDaysWorked.divide(
                MONTHS_OF_YEAR.toBigDecimal(),
                2,
                RoundingMode.HALF_UP
            )

        return result.setScale(2, RoundingMode.HALF_UP)
    }

    fun getDecimoTercerSueldoMensualizado(salaryAtMonth: BigDecimal): BigDecimal {
        val result =
            salaryAtMonth.divide(
                MONTHS_OF_YEAR.toBigDecimal(),
                2,
                RoundingMode.HALF_UP
            )
        return result.setScale(2, RoundingMode.HALF_UP)
    }

    fun getDecimoCuartoSueldoMensualizadoFiniquito(
        startDate: String,
        endDate: String,
        hoursWorked: Int
    ): BigDecimal {
        val idWorkday = if (hoursWorked >= WEEKLY_HOURS_COMPLETE) COMPLETE else PARTIAL
        val numberDaysWorked = numberDaysWorked(endDate, startDate)

        val salary =
            if (idWorkday == PARTIAL) calculateSalaryEquivalent(hoursWorked) else SBU.toBigDecimal()

        val divide = (salary.toDouble() / DAYS_OF_YEAR)
        val result =
            divide * numberDaysWorked
        return result.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
    }


}
