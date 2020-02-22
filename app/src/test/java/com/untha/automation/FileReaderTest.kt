package com.untha.automation

import com.untha.applicationservices.CalculatorsService
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class FileReaderTest(
    private val excelModel: ExcelModel
) {
    val calculatorsService = CalculatorsService()

    companion object {
        var manageFile = FileReader()
        val excelData = manageFile.excelModels
        @JvmStatic
        @Parameterized.Parameters
        fun data() = excelData
    }

    @Test
    fun `should return decimo tercero mensualizado`() {

        val decimoTerceroMensualizado =
            calculatorsService.getDecimoTercerSueldoMensualizado(excelModel.finalSalary)
        assertThat(decimoTerceroMensualizado, equalTo(excelModel.decimoTerceroMensualizado))
    }

    @Test
    fun `should return decimo cuarto mensualizado`() {

        val decimoCuartoMensualizado = calculatorsService.getDecimoCuartoSueldoMensualizado(
            excelModel.idWorkday,
            excelModel.weekHours.toInt()
        )
        assertThat(decimoCuartoMensualizado, equalTo(excelModel.decimoCuartoMensualizado))
    }

    @Test
    fun `should return decimo cuarto acumulado`() {
        val decimoCuarto = calculatorsService.getDecimoCuartoAcumulado(
            excelModel.startDate,
            excelModel.endDate,
            excelModel.idWorkday,
            excelModel.idArea,
            excelModel.weekHours.toInt()
        )
        assertThat(decimoCuarto, equalTo(excelModel.decimoCuartoAcumulado))
    }

    @Test
    fun `should return decimo tercer acumulado`() {
        val decimoTercero = calculatorsService.getDecimoTercerSueldoAcumulado(
            excelModel.finalSalary, excelModel.startDate, excelModel.endDate
        )
        assertThat(decimoTercero, equalTo(excelModel.decimoTerceroAcumulado))
    }

    @Test
    fun `should return IESS`() {
        val IESS = calculatorsService.getAportacionMensualIESS(excelModel.finalSalary)
        assertThat(IESS, equalTo(excelModel.percentageIESS))
    }

    @Test
    fun `should return fondos de reserva`() {
        val fondosReserva = calculatorsService.getFondoReservaMensualizado(
            excelModel.startDate,
            excelModel.endDate,
            excelModel.finalSalary
        )
        assertThat(fondosReserva, equalTo(excelModel.fondosReserva))
    }

    @Test
    fun isStringSame() {
        val date = "08-Jun-2012"
        val stringExpected = "2012-06-08"

        val fileReader = FileReader()
        val result = fileReader.changeFormatDate(date)

        assertThat(result, equalTo(stringExpected))

    }
}



