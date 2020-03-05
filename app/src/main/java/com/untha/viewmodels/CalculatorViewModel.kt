package com.untha.viewmodels

import androidx.lifecycle.ViewModel
import com.untha.applicationservices.CalculatorDecimosService
import com.untha.applicationservices.CalculatorIESSService
import com.untha.utils.salaryForDaysWorked
import java.math.BigDecimal

class CalculatorViewModel : ViewModel() {
    var calculatorService: CalculatorDecimosService = CalculatorDecimosService()
    var calculatorIESSService: CalculatorIESSService = CalculatorIESSService()

    fun getAportacionMensualIESS(startDate: String, endDate: String, salary: String): String {
        return calculatorIESSService.getAportacionMensualIESS(
            startDate,
            endDate,
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

    fun getDecimoTerceroMensualizado(
        startDate: String,
        endDate: String,
        salary: Double
    ): BigDecimal {
        return calculatorService.getDecimoTercerSueldoFiniquitoMensualizado(startDate, endDate, salary)
    }

    fun getDecimoCuartoMensualizado(
        idWorkday: Int,
        numberOfHoursWeekly: Int,
        startDate: String,
        endDate: String
    ): BigDecimal {
        return calculatorService.getDecimoCuartoSueldoMensualizado(
            idWorkday,
            numberOfHoursWeekly
        )
    }

    fun getSalary(
        startDate: String,
        endDate: String,
        salary: BigDecimal
    ): BigDecimal {
        return salaryForDaysWorked(
            startDate,
            endDate,
            salary
        )
    }
}
