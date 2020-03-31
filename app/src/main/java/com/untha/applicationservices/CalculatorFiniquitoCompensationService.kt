package com.untha.applicationservices

import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorFiniquitoCompensationService {
    companion object {
        const val EMBARAZO_NUMBER_SALARIES = 12
        const val DISCAPACIDAD_NUMBER_SALARIES = 18
        const val HINT_EMBARAZO = "R3P1R3"
        const val HINT_DISCAPACIDAD = "R3P1R1"
        const val HINT_SUSTITUTO = "R3P1R2"
    }

    fun getCompensation(hint: String, salary: BigDecimal): BigDecimal {

        if (hint == HINT_EMBARAZO) {
            return EMBARAZO_NUMBER_SALARIES.toBigDecimal().multiply(salary)
                .setScale(2, RoundingMode.HALF_UP)
        }
        if (hint == HINT_DISCAPACIDAD || hint == HINT_SUSTITUTO) {
            return DISCAPACIDAD_NUMBER_SALARIES.toBigDecimal().multiply(salary)
                .setScale(2, RoundingMode.HALF_UP)
        }
        return 0.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

    }
}
