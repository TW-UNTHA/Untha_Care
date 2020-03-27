package com.untha.applicationservices

import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.calculateNumberOfDayBetween
import com.untha.utils.stringToCalendar
import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorFiniquitoCausalesService {
    companion object {
        const val PERCENTAGE_DESAHUCIO_PER_YEAR = 0.25
        const val TRIAL_PERIOD_RETRIBUTION_OR_EMPLOYEE_DEAD = 0
        const val DESAHUCIO_HINT = "R3P2R1"
        const val TRIAL_PERIOD_HINT = "R3P2R2"
        const val EMPLOYEE_DEAD_HINT = "R3P2R3"
        const val INTEMPESTIVO_HINT = "R3P2R5"
        const val MIN_YEARS_INTEMPESTIVO = 3
        const val MAX_YEARS_INTEMPESTIVO = 25
        const val ROUNDING_SCALE_MAXIMUN =3
        const val MUERTE_EMPLEADOR_HINT = "R3P2R4"

    }

    fun getCausalRetribution(
        hint: String,
        salary: BigDecimal,
        startDate: String,
        endDate: String
    ): BigDecimal {

        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)
        var valueOfCausal: BigDecimal = BigDecimal.ZERO

        if (hint == DESAHUCIO_HINT) {
            val yearsDesahucio =
                (calculateNumberOfDayBetween(calendarStartDate, calendarEndDate).toBigDecimal()
                    .divide(DAYS_OF_YEAR.toBigDecimal(), ROUNDING_SCALE_MAXIMUN, RoundingMode.HALF_DOWN)).toInt()

            valueOfCausal = salary.multiply(PERCENTAGE_DESAHUCIO_PER_YEAR.toBigDecimal())
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(yearsDesahucio.toBigDecimal()).setScale(2, RoundingMode.HALF_UP)
        }
        if (hint == TRIAL_PERIOD_HINT || hint == EMPLOYEE_DEAD_HINT) {
            valueOfCausal = TRIAL_PERIOD_RETRIBUTION_OR_EMPLOYEE_DEAD.toBigDecimal()
                .setScale(2, RoundingMode.HALF_UP)
        }

        if (hint == INTEMPESTIVO_HINT || hint == MUERTE_EMPLEADOR_HINT) {
            var years =
                calculateNumberOfDayBetween(calendarStartDate, calendarEndDate).toBigDecimal()
                    .divide(DAYS_OF_YEAR.toBigDecimal(), ROUNDING_SCALE_MAXIMUN, RoundingMode.HALF_UP)

            if (years <= MIN_YEARS_INTEMPESTIVO.toBigDecimal().setScale(ROUNDING_SCALE_MAXIMUN, RoundingMode.HALF_UP)) {
                valueOfCausal = salary.multiply(MIN_YEARS_INTEMPESTIVO.toBigDecimal())
                    .setScale(2, RoundingMode.HALF_UP)
            }
            if (years >= MAX_YEARS_INTEMPESTIVO.toBigDecimal().setScale(ROUNDING_SCALE_MAXIMUN, RoundingMode.HALF_UP)) {
                valueOfCausal = salary.multiply(MAX_YEARS_INTEMPESTIVO.toBigDecimal())
                    .setScale(2, RoundingMode.HALF_UP)

            } else if(years> MIN_YEARS_INTEMPESTIVO.toBigDecimal() && years < MAX_YEARS_INTEMPESTIVO.toBigDecimal()){

                years = years.setScale(0, RoundingMode.CEILING)
                valueOfCausal = salary.multiply(years).setScale(2, RoundingMode.HALF_UP)
            }
        }
        return valueOfCausal
    }
}

