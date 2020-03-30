package com.untha.automation

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.untha.applicationservices.CalculatorDecimosService
import com.untha.applicationservices.CalculatorIESSService
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.utils.Constants
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import java.math.RoundingMode

@RunWith(Parameterized::class)
class FileReaderTest(
    private val excelModel: ExcelModel
) : KoinTest {
    private lateinit var calculatorsService: CalculatorDecimosService
    private lateinit var calculatorIESSService: CalculatorIESSService

    private val sharedPreferences by inject<SharedPreferences>()
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        startKoin {
            modules(
                listOf(
                    persistenceModule,
                    networkModule,
                    viewModelsModule,
                    mapperModule
                )
            )
        }
        declareMock<SharedPreferences>()
        calculatorsService = CalculatorDecimosService(sharedPreferences)
        calculatorIESSService = CalculatorIESSService(sharedPreferences)

        val constantsJson = "{\n" +
                "  \"version\": 1,\n" +
                "  \"sbu\": 400,\n" +
                "  \"percentage_iess_afiliado\": 0.0945,\n" +
                "  \"percentage_fondos_reserva\": 0.0833,\n" +
                "  \"hours_complete_time\": 40\n" +
                "\n" +
                "}"


        Mockito.`when`(sharedPreferences.getString(Constants.CONSTANTS, ""))
            .thenReturn(constantsJson)


    }

    @After
    fun after() {
        stopKoin()
    }


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
