package com.untha.viewmodels

import androidx.lifecycle.ViewModel
import com.untha.applicationservices.CalculatorsService
import java.math.BigDecimal

class CalculatorViewModel : ViewModel() {
    var calculatorService: CalculatorsService = CalculatorsService()

    fun getAportacionMensualIESS(salary: String): String {
        return calculatorService.getAportacionMensualIESS(salary.toBigDecimal()).toString()
    }

    fun getFondoReservaMensualizado(
        startDate: String,
        endDate: String,
        salary: String
    ): BigDecimal? {
        return calculatorService.getFondoReservaMensualizado(
            startDate,
            endDate,
            salary.toBigDecimal()
        )
    }

    fun getDecimoTercerSueldoAcumulado(
        startDate: String,
        endDate: String,
        salary: String
    ): BigDecimal {
        return calculatorService.getDecimoTercerSueldoAcumulado(
            salary.toBigDecimal(),
            startDate,
            endDate
        )
    }

    fun getDecimoCuartoAcumulado(
        startDate: String,
        endDate: String,
        idWorkDay: Int,
        idArea: Int,
        numberOfHoursWeekly: Int = 0
    ): BigDecimal? {
        return calculatorService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            idWorkDay,
            idArea,
            numberOfHoursWeekly
        )
    }

    fun getDecimoTerceroMensualizado(salary: BigDecimal): BigDecimal {
        return calculatorService.getDecimoTercerSueldoMensualizado(salary)
    }

    fun getDecimoCuartoMensualizado(idWorkday: Int, numberOfHoursWeekly: Int): BigDecimal {
        return calculatorService.getDecimoCuartoSueldoMensualizado(idWorkday, numberOfHoursWeekly)
    }
}
