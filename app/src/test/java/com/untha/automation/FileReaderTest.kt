package com.untha.automation

import com.untha.applicationservices.CalculatorDecimosService
import com.untha.applicationservices.CalculatorIESSService
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.RoundingMode

@RunWith(Parameterized::class)
class FileReaderTest(
    private val excelModel: ExcelModel
) {
    val calculatorsService = CalculatorDecimosService()
    val calculatorIESSService = CalculatorIESSService()

    companion object {
        var manageFile = FileReader()
        val excelData = manageFile.excelModels
        @JvmStatic
        @Parameterized.Parameters
        fun data() = excelData
    }

    @Test
    fun `should match decimo tercero mensualizado value excel with result`() {
        val expected = excelModel.decimoTerceroMensualizado

        val resultDecimoTerceroMensualizado =
            calculatorsService.getDecimoTercerSueldoMensualizado(
                excelModel.finalSalary
            )

        assertThat(resultDecimoTerceroMensualizado, equalTo(expected))
    }

    @Test
    fun `should match decimo cuarto mensualizado value excel with result`() {
        val expected = excelModel.decimoCuartoMensualizado

        val resultDecimoCuartoMensualizado = calculatorsService.getDecimoCuartoSueldoMensualizado(
            excelModel.idWorkday,
            excelModel.weekHours.toInt()
        )

        assertThat(resultDecimoCuartoMensualizado, equalTo(expected))
    }

    @Test
    fun `should match decimo cuarto acumulado value excel with result`() {
        val expected = excelModel.decimoCuartoAcumulado

        val resultDecimoCuarto = calculatorsService.getDecimoCuartoAcumulado(
            excelModel.startDate,
            excelModel.endDate,
            excelModel.idWorkday,
            excelModel.idArea,
            excelModel.weekHours.toInt()
        )

        assertThat(resultDecimoCuarto, equalTo(expected))
    }

    @Test
    fun `should match decimo tercero acumulado value excel with result`() {
        val expected = excelModel.decimoTerceroAcumulado

        val resultDecimoTercero = calculatorsService.getDecimoTercerSueldoAcumulado(
            excelModel.finalSalary, excelModel.startDate, excelModel.endDate
        )

        assertThat(resultDecimoTercero, equalTo(expected))
    }

    @Test
    fun `should match percentage IESS value excel with result`() {
        val expected = excelModel.percentageIESS.setScale(2, RoundingMode.HALF_UP)
        val resultIESS = calculatorIESSService.getAportacionMensualIESS(
            excelModel.finalSalary
        )

        assertThat(resultIESS, equalTo(expected))
    }

    @Test
    fun `should match fondos de reserva value excel with result`() {
        val expected = excelModel.fondosReserva

        val resultFondosReserva = calculatorIESSService.getFondoReservaMensualizado(
            excelModel.startDate,
            excelModel.endDate,
            excelModel.finalSalary
        )
        assertThat(resultFondosReserva, equalTo(expected))
    }
}



