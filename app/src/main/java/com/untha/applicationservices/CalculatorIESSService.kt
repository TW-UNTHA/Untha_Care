package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.ConstantsCalculators.PERCENTAJE_APORTE_FONDOS_RESERVA
import com.untha.utils.ConstantsCalculators.PERCENTAJE_APORTE_IESS_PRIVADO
import com.untha.utils.calculateNumberOfDayBetween
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
}
