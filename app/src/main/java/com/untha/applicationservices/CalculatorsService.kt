package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.ConstantsCalculators.MONTHS_OF_YEAR
import com.untha.utils.ConstantsCalculators.PERCENTAJE_APORTE_FONDOS_RESERVA
import com.untha.utils.ConstantsCalculators.PERCENTAJE_APORTE_IESS_PRIVADO
import com.untha.utils.ConstantsCalculators.SBU
import com.untha.utils.ConstantsCalculators.WEEKLY_HOURS_COMPLETE
import com.untha.utils.calculateNumberOfDayBetween
import com.untha.utils.equivalentOfAnnualLeavesToDay
import com.untha.utils.getAge
import com.untha.utils.isFirstDayOfDecember
import com.untha.utils.isFirstDayOfMarch
import com.untha.utils.isLastDayOfFebruary
import com.untha.utils.isLastDayOfNovember
import com.untha.utils.lastDayOfFebruary
import com.untha.utils.newStartDatePeriodOfWork
import com.untha.utils.numberOfAnnualLeavesPerAge
import com.untha.utils.stringToCalendar
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class CalculatorsService {

    companion object {
        const val SIERRA_ORIENTE = 2
        const val COSTA_GALAPAGOS = 1
        const val COMPLETA = 1
        const val PARCIAL = 2
        const val EIGHTEEN_AGE = 18
        const val SEVENTEEN_AGE = 17
        const val FIFTEEN_AGE = 15
        const val SIXTEEN_AGE = 16
        const val TWENTY_AGE = 20
        const val START_INDEX = 4
        const val END_INDEX = 10

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

        when (isFirstDayOfDecember(calendarStartDate) && isLastDayOfNovember(calendarEndDate)) {
            true -> return salary.setScale(2, RoundingMode.HALF_UP)

            false -> {
                val newStartDate = setStartDateDecimoTercero(calendarEndDate, calendarStartDate)
                return calculateDecimoTerceroAcumulado(salary, newStartDate, calendarEndDate)
            }

        }
    }

    fun getDecimoCuartoSueldoMensualizado(
        idWorkday: Int,
        numberOfHoursWeekly: Int = 0
    ): BigDecimal {
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
            calculateNumberOfDayBetween(stringToCalendar(startDate), stringToCalendar(endDate))
        if (numberOfDays > DAYS_OF_YEAR) {
            val result = salary.multiply(PERCENTAJE_APORTE_FONDOS_RESERVA.toBigDecimal())
            return result.setScale(2, RoundingMode.HALF_UP)
        } else {
            return 0.00.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        }
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
    ): BigDecimal? {

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
    ): BigDecimal? {
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




    fun getNumberAnnualLeaveDay(startDate: String, endDate: String, age: Int): Double {
        val numberAnnualLeaveAtYear = numberOfAnnualLeavesPerAge(age)
        val numberAnnualLeaveAtDay = equivalentOfAnnualLeavesToDay(numberAnnualLeaveAtYear)
        val startDate = newStartDatePeriodOfWork(startDate, endDate)
        val numberDayWorked =
            calculateNumberOfDayBetween(stringToCalendar(startDate), stringToCalendar(endDate))

        return numberDayWorked * numberAnnualLeaveAtDay
    }


    fun getDailyPayAnnualLeave(salary: Double): BigDecimal {
        val salaryAtDay = (salary * MONTHS_OF_YEAR) / DAYS_OF_YEAR
        return salaryAtDay.toBigDecimal()
    }

    fun getCostOfAnnualLeaveDays(
        startDate: String,
        endDate: String,
        salary: Double,
        age: Int,
        daysTaken: Int
    ): BigDecimal {
        val numberAnnualLeaveDays = getNumberAnnualLeaveDay(startDate, endDate, age)
        val numberAnnualLeaveDaysAvailable = numberAnnualLeaveDays - daysTaken
        val salaryAtDay = getDailyPayAnnualLeave(salary)

        val result = salaryAtDay.multiply(
            numberAnnualLeaveDaysAvailable.toBigDecimal().setScale(
                2,
                RoundingMode.HALF_UP
            )
        )
        return result
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

    fun getTotalCostFirstPeriod(
        startDate: String,
        endDate: String,
        birthDate: String,
        salary: Double,
        daysTaken: Int
    ): BigDecimal {
        val endDateByPeriod = getNewDate(startDate, endDate, birthDate)

        val age = getAge(birthDate, startDate)
        val cost =
            getCostOfAnnualLeaveDays(startDate, endDateByPeriod, salary, age, daysTaken)


        return cost.setScale(2, RoundingMode.HALF_UP)
    }

    fun getTotalCostSecondPeriod(
        startDate: String,
        endDate: String,
        birthDate: String,
        salary: Double,
        daysTaken: Int
    ): BigDecimal {
        val startDateByPeriod = getNewDate(startDate, endDate, birthDate)
        val age = getAge(birthDate, startDateByPeriod)

        val cost =
            getCostOfAnnualLeaveDays(startDateByPeriod, endDate, salary, age, daysTaken)

        return cost.setScale(2, RoundingMode.HALF_UP)
    }

    fun getTotalCostPeriod(
        startDate: String,
        endDate: String,
        birthDate: String,
        salary: Double,
        daysTaken: Int
    ): BigDecimal {
        var cost: BigDecimal
        val newDate = getNewDate(startDate, endDate, birthDate)
        cost = getTotalCostFirstPeriod(startDate, endDate, birthDate, salary, daysTaken)
            .setScale(2, RoundingMode.HALF_UP)
        if (newDate.equals(startDate) || newDate.equals(endDate)) {
            return cost
        }
        cost += getTotalCostSecondPeriod(startDate, endDate, birthDate, salary, daysTaken)

        return cost.setScale(2, RoundingMode.HALF_UP)
    }


}
