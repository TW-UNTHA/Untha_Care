package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.verify
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.utils.Constants
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import java.math.RoundingMode

@RunWith(JUnit4::class)
class CalculatorViewModelTest : KoinTest {
    private val sharedPreferences by inject<SharedPreferences>()
    val constantsJson = "{\n" +
            "  \"version\": 1,\n" +
            "  \"sbu\": 400,\n" +
            "  \"percentage_iess_afiliado\": 0.0945,\n" +
            "  \"percentage_fondos_reserva\": 0.0833,\n" +
            "  \"hours_complete_time\": 40\n" +
            "}\n"
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
    }

    @After
    fun after() {
        stopKoin()
    }


    @Test
    fun `should return aportacion mensual`() {
        val salary = "400"
        val calculatorViewModel = CalculatorViewModel(sharedPreferences)
        Mockito.`when`(sharedPreferences.getString(Constants.CONSTANTS, ""))
            .thenReturn(constantsJson)

        val result = calculatorViewModel.getAportacionMensualIESS(salary)

        assertThat(result, equalTo("37.80"))
        verify(sharedPreferences).getString(Constants.CONSTANTS, "")
    }

    @Test
    fun `should return fondo de reserva mensualizado`() {
        val salary = "500"
        val startDate = "2019-01-01"
        val endDate = "2019-01-21"
        val fondoReservaExpected = 0.00.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val calculatorViewModel = CalculatorViewModel(sharedPreferences)

        val result = calculatorViewModel.getFondoReservaMensualizado(startDate, endDate, salary)

        assertThat(result, equalTo(fondoReservaExpected))
    }

    @Test
    fun `should return not zero`() {
        val salary = "500"
        val startDate = "2018-01-01"
        val endDate = "2019-01-21"
        val fondoReservaExpected = 41.65.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val calculatorViewModel = CalculatorViewModel(sharedPreferences)
        Mockito.`when`(sharedPreferences.getString(Constants.CONSTANTS, ""))
            .thenReturn(constantsJson)

        val result = calculatorViewModel.getFondoReservaMensualizado(startDate, endDate, salary)

        assertThat(result, equalTo(fondoReservaExpected))
        verify(sharedPreferences).getString(Constants.CONSTANTS, "")
    }

    @Test
    fun `should return decimo tercer sueldo acumulado`() {
        val salary = "500"
        val startDate = "2018-01-01"
        val endDate = "2019-01-21"
        val decimoTercerExpected = 69.44.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val calculatorViewModel = CalculatorViewModel(sharedPreferences)

        val result = calculatorViewModel.getDecimoTercerSueldoAcumulado(startDate, endDate, salary)

        assertThat(result, equalTo(decimoTercerExpected))
    }

    @Test
    fun `should return decimo cuarto acumulado`() {
        val startDate = "2018-01-01"
        val endDate = "2019-01-21"
        val idArea = 1
        val numberOfHoursWeek = 30
        val decimoCuartoExpected = 266.67.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val calculatorViewModel = CalculatorViewModel(sharedPreferences)
        Mockito.`when`(sharedPreferences.getString(Constants.CONSTANTS, ""))
            .thenReturn(constantsJson)

        val result = calculatorViewModel.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            idArea,
            numberOfHoursWeek
        )

        assertThat(result, equalTo(decimoCuartoExpected))
    }

    @Test
    fun `should return decimo tercero mensualizado`() {
        val salary = "500"
        val decimoCuartoMensualizadoExpected =
            41.67.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val calculatorViewModel = CalculatorViewModel(sharedPreferences)
        Mockito.`when`(sharedPreferences.getString(Constants.CONSTANTS, ""))
            .thenReturn(constantsJson)

        val result = calculatorViewModel.getDecimoTerceroMensualizado(
            salary.toBigDecimal()
        )

        assertThat(result, equalTo(decimoCuartoMensualizadoExpected))
    }

    @Test
    fun `should return decimo cuarto mensualizado`() {
        val numberOfHoursWeek = 30
        val decimoCuartoMensualizadoExpected =
            25.00.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val calculatorViewModel = CalculatorViewModel(sharedPreferences)
        Mockito.`when`(sharedPreferences.getString(Constants.CONSTANTS, ""))
            .thenReturn(constantsJson)

        val result = calculatorViewModel.getDecimoCuartoMensualizado(
            numberOfHoursWeek
        )

        assertThat(result, equalTo(decimoCuartoMensualizadoExpected))
    }
}
