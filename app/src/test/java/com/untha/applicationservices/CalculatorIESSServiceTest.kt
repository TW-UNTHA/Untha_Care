package com.untha.applicationservices

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.utils.Constants
import com.untha.utils.ConstantsValues
import junit.framework.Assert.assertEquals
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
import java.math.BigDecimal
import java.math.RoundingMode

@RunWith(JUnit4::class)
class CalculatorIESSServiceTest : KoinTest{

    private val sharedPreferences by inject<SharedPreferences>()
    lateinit var calculatorIESSService: CalculatorIESSService
    companion object{
        const val PERCENTAGE_APORTE_IESS_PRIVADO = 0.0945
    }


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
        calculatorIESSService = CalculatorIESSService(sharedPreferences)
        val constantsJson ="{\n" +
                "  \"version\": 1,\n" +
                "  \"sbu\": 400,\n" +
                "  \"percentage_iess_afiliado\": 0.0945,\n" +
                "  \"percentage_fondos_reserva\": 0.0833,\n" +
                "  \"hours_complete_time\": 40\n" +
                "\n" +
                "}"

        ConstantsValues(sharedPreferences)

        Mockito.`when`(sharedPreferences.getString(Constants.CONSTANTS, "")).thenReturn(constantsJson)
    }

    @After
    fun after() {
        stopKoin()
    }


    @Test
    fun `should return 37,80 of IESS contribution  when my salary is 400`() {
        val salary = 500.00.toBigDecimal()
        val percentageIESSExpected =
            salary.multiply(PERCENTAGE_APORTE_IESS_PRIVADO.toBigDecimal())
                .setScale(2, RoundingMode.HALF_UP)


        val result =
            calculatorIESSService.getAportacionMensualIESS(salary)

        assertEquals(percentageIESSExpected, result)
    }


    @Test
    fun `should return 0 of fondo de reserva contribution when period is less than one year`() {
        val expectedValue = BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-01-01"
        val endDate = "2019-12-30"

        val result = calculatorIESSService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 37,49 of fondo de reserva contribution when period is one year`() {
        val expectedValue = BigDecimal(0).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-01-01"
        val endDate = "2020-01-01"

        val result = calculatorIESSService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 0,00 of fondo de reserva contribution when period is more than one year`() {
        val expectedValue = BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(400.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-03-01"

        val result = calculatorIESSService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 24,99 of fondo de reserva contribution when period is more than one year`() {
        val expectedValue = BigDecimal(24.99).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(300.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-03-02"

        val result = calculatorIESSService.getFondoReservaMensualizado(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return contribution IESS finiquito with 2019-03-01 and 2020-03-02`() {
        val expectedValue = BigDecimal(1.89).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(300.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-03-02"

        val result =
            calculatorIESSService.getAportacionMensualIESSFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return contribution IESS finiquito with 2019-03-01 and 2020-03-25`() {
        val expectedValue = BigDecimal(23.63).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(300.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-03-01"
        val endDate = "2020-03-25"

        val result =
            calculatorIESSService.getAportacionMensualIESSFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return contribution IESS finiquito with 2019-02-01 and 2020-02-29`() {
        val expectedValue = BigDecimal(28.35).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(300.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-02-01"
        val endDate = "2020-02-29"

        val result =
            calculatorIESSService.getAportacionMensualIESSFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }


    @Test
    fun `should return contribution IESS finiquito with 2020-02-04 and 2020-02-21`() {
        val expectedValue = BigDecimal(17.01).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(300.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2020-02-04"
        val endDate = "2020-02-21"

        val result =
            calculatorIESSService.getAportacionMensualIESSFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should return 0 of fondo de reserva contribution finiquito when period is less than one year`() {
        val expectedValue = BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-01-01"
        val endDate = "2019-12-30"

        val result = calculatorIESSService.getFondoReservaMensualizadoFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }
    @Test
    fun `should fondo de reserva contribution finiquito when period more than one year start date 2018-11-01 end date 2019-12-30`() {
        val expectedValue = BigDecimal(37.49).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2018-11-01"
        val endDate = "2019-12-30"

        val result = calculatorIESSService.getFondoReservaMensualizadoFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `should fondo de reserva contribution finiquito when period more than one year start date 2019-05-14 end date 2019-05-28`() {
        val expectedValue = BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP)
        val salary = BigDecimal(450.00).setScale(2, RoundingMode.HALF_UP)
        val startDate = "2019-05-14"
        val endDate = "2019-05-28"

        val result = calculatorIESSService.getFondoReservaMensualizadoFiniquito(startDate, endDate, salary)

        assertEquals(expectedValue, result)
    }
}
