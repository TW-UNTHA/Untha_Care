package com.untha.viewmodels

import androidx.lifecycle.ViewModel
import com.untha.applicationservices.CalculatorDecimosService
import com.untha.applicationservices.CalculatorIESSService
import java.math.BigDecimal

class CalculatorViewModel : ViewModel() {
    var calculatorService: CalculatorDecimosService = CalculatorDecimosService()
    var calculatorIESSService: CalculatorIESSService = CalculatorIESSService()

    fun getAportacionMensualIESS( salary: String): String {
        return calculatorIESSService.getAportacionMensualIESS(
            salary.toBigDecimal()
        ).toString()
    }

    fun getFondoReservaMensualizado(
        startDate: String,
        endDate: String,
        salary: String
    ): BigDecimal? {
        return calculatorIESSService.getFondoReservaMensualizado(
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
        idArea: Int,
        numberOfHoursWeekly: Int = 0
    ): BigDecimal? {
        return calculatorService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            idArea,
            numberOfHoursWeekly
        )
    }

    fun getDecimoTerceroMensualizado(
        salary: BigDecimal
    ): BigDecimal {
        return calculatorService.getDecimoTercerSueldoMensualizado(salary)
    }

    fun getDecimoCuartoMensualizado(
        numberOfHoursWeekly: Int
    ): BigDecimal {
        return calculatorService.getDecimoCuartoSueldoMensualizado(
            numberOfHoursWeekly
        )
    }
}

