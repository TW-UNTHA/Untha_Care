package com.untha.automation

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.mappers.CategoryMapper
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.utils.Constants
import com.untha.viewmodels.CalculatorFiniquitoResultsViewModel
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

@RunWith(Parameterized::class)
class FileReaderFiniquitoTest(private val excelModel: ExcelModelFiniquito) : KoinTest {

    private val mockCategoryWithRelationsRepository by inject<CategoryWithRelationsRepository>()
    private val mockCategoryMapper by inject<CategoryMapper>()
    private val sharedPreferences by inject<SharedPreferences>()
    private lateinit var calculatorFiniquitoResultViewModel: CalculatorFiniquitoResultsViewModel

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
        declareMock<CategoryWithRelationsRepository>()
        declareMock<CategoryMapper>()
        declareMock<SharedPreferences>()

        calculatorFiniquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences, mockCategoryWithRelationsRepository, mockCategoryMapper
        )
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
        var manageFile = FileReaderFiniquito()
        val excelData = manageFile.excelModels
        @JvmStatic
        @Parameterized.Parameters
        fun data() = excelData
    }

    @Test
    fun `should match decimo tercero value excel with result`() {

        val expected = excelModel.decimoTercero


        val resultDecimoTercero =
            calculatorFiniquitoResultViewModel.getDecimoTercero(
                excelModel.decimoTerceroOption,
                excelModel.startDate,
                excelModel.endDate,
                excelModel.salario
            )
        assertThat(resultDecimoTercero, equalTo(expected))

    }

    @Test
    fun `should match decimo cuarto value excel with result`() {

        val expected = excelModel.decimoCuarto


        val resultDecimoCuarto =
            calculatorFiniquitoResultViewModel.getDecimoCuarto(
                excelModel.decimoCuartoOption,
                excelModel.startDate,
                excelModel.endDate,
                excelModel.idArea,
                excelModel.horasSemana
            )

        assertThat(resultDecimoCuarto, equalTo(expected))
    }

    @Test
    fun `should match fondos de reserva value excel with result`() {

        val expected = excelModel.fondosReserva

        val resultFondosReserva =
            calculatorFiniquitoResultViewModel.getFondosReserva(
                excelModel.fondosReservaOption,
                excelModel.startDate,
                excelModel.endDate,
                excelModel.salario
            )

        assertThat(resultFondosReserva, equalTo(expected))
    }

    @Test
    fun `should match anual leave value excel with result`() {
        val expected = excelModel.vacationPay

        val resultAnualLeave =
            calculatorFiniquitoResultViewModel.getVacationsNotTaken(
                excelModel.daysTaken,
                excelModel.startDate,
                excelModel.endDate,
                excelModel.birthDate,
                excelModel.salario.toDouble()
            )

        assertThat(resultAnualLeave, equalTo(expected))
    }

    @Test
    fun `should match final salary value excel with result`() {

        val expected = excelModel.finalSalary

        val resultFinalSalary =
            calculatorFiniquitoResultViewModel.getSalaryLastMonth(
                excelModel.salario,
                excelModel.endDate,
                excelModel.startDate
            )


        assertThat(resultFinalSalary, equalTo(expected))
    }

    @Test
    fun `should match compensation value excel with result`() {

        val expected = excelModel.compensation

        val resultCompensation =
            calculatorFiniquitoResultViewModel.getIndemnizacion(
                excelModel.typeIndemnizacion,
                excelModel.salario
            )

        assertThat(resultCompensation, equalTo(expected))
    }

    @Test
    fun `should match causal value excel with result`() {

        val expected = excelModel.causal

        val resultCausal =
            calculatorFiniquitoResultViewModel.getDesahucio(
                excelModel.typeCausal,
                excelModel.salario,
                excelModel.startDate,
                excelModel.endDate
            )

        assertThat(resultCausal, equalTo(expected))
    }
}
