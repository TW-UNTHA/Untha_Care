package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.DAYS_IN_MONTH
import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.ConstantsCalculators.PERCENTAJE_APORTE_FONDOS_RESERVA
import com.untha.utils.ConstantsCalculators.PERCENTAJE_APORTE_IESS_PRIVADO
import com.untha.utils.calculateNumberOfDayBetween
import com.untha.utils.numberDaysWorked
import com.untha.utils.stringToCalendar
import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorIESSService {

    fun getAportacionMensualIESS(
        salaryAtMonth: BigDecimal
    ): BigDecimal? {
        val result =
            salaryAtMonth.multiply(PERCENTAJE_APORTE_IESS_PRIVADO.toBigDecimal())

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

    fun getFondoReservaMensualizadoFiniquito(
        startDate: String,
        endDate: String,
        salary: BigDecimal
    ): BigDecimal? {
        val numberOfDaysBetweenDates =
            calculateNumberOfDayBetween(stringToCalendar(startDate), stringToCalendar(endDate))
        if (numberOfDaysBetweenDates > DAYS_OF_YEAR) {
            val numberDaysWorked = numberDaysWorked(endDate, startDate)
            val salaryForDay = salary.divide(DAYS_IN_MONTH.toBigDecimal())
            val result =
                numberDaysWorked.toBigDecimal() * salaryForDay * PERCENTAJE_APORTE_FONDOS_RESERVA.toBigDecimal()
            return result.setScale(2, RoundingMode.HALF_UP)
        }
        return 0.00.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

    }

    fun getAportacionMensualIESSFiniquito(
        startDate: String,
        endDate: String,
        salary: BigDecimal
    ): BigDecimal {
        val numberDaysWorked = numberDaysWorked(endDate, startDate)
        val salaryForDay = salary.divide(DAYS_IN_MONTH.toBigDecimal())

        val salaryForDayWorked =
            salaryForDay * numberDaysWorked.toBigDecimal() * PERCENTAJE_APORTE_IESS_PRIVADO.toBigDecimal()
        return salaryForDayWorked.setScale(2, RoundingMode.HALF_UP)
    }
}
