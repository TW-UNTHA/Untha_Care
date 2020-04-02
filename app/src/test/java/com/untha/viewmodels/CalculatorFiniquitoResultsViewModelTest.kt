package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.ResultCalculatorWrapper
import com.untha.model.transactionalmodels.ResultWrapper
import com.untha.utils.Constants
import com.utils.MockObjects
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers
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
class CalculatorFiniquitoResultsViewModelTest:KoinTest{

    private val sharedPreferences by inject<SharedPreferences>()
    private val mockCategoryWithRelationsRepository by inject<CategoryWithRelationsRepository>()
    private val mockCategoryMapper by inject<CategoryMapper>()

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
        declareMock<CategoryMapper>()
        declareMock<CategoryWithRelationsRepository>()
        declareMock<SharedPreferences>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should retrieve dinamic results from shared preferences`(){

        val calculatorViewModel = CalculatorFiniquitoResultsViewModel(sharedPreferences, mockCategoryWithRelationsRepository, mockCategoryMapper)
        val json = "{\n" +
                "  \"version\": 1,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"id\": \"F1\",\n" +
                "      \"type\": \"fault\",\n" +
                "      \"content\": \"ADVERTENCIA: Si trabajas 40 horas semanales(jornada completa), debes recibir el pago del salario básico unificado vigente, si trabajas más debes recibir un pago extra por cada hora\",\n" +
                "      \"categories\": [\n" +
                "        8\n" +
                "      ]\n" +
                "    }]}"

        Mockito.`when`(sharedPreferences.getString(Constants.CALCULATOR_ROUTE_RESULT, "")).thenReturn(json)
        val result = Json.parse(ResultWrapper.serializer(), json)


        calculatorViewModel.loadResultDynamicFromSharePreferences()

        assertThat(calculatorViewModel.resultCalculatorFaults,equalTo(result.results))
    }

    @Test
    fun `should retrieve static results from shared preferences`(){

        val calculatorViewModel = CalculatorFiniquitoResultsViewModel(sharedPreferences, mockCategoryWithRelationsRepository, mockCategoryMapper)
        val json = "{\n" +
                "  \"version\": 1,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"id\": \"R1\",\n" +
                "      \"title\": \"Seguro de desempleo\",\n" +
                "      \"content\": \"Para acceder al seguro de desempleo debes:<br> <br> •&nbsp;&nbsp;&nbsp; Encontrarte en situación de desempleo por un periodo mínimo de 60 días. <br>•&nbsp;&nbsp;&nbsp; Tener 24 aportaciones no simultáneas en relación de dependencia.<br> •&nbsp;&nbsp;&nbsp; Tener 6 aportaciones  continuas e inmediatamente anteriores a la situación de desempleo. <br>•&nbsp;&nbsp;&nbsp; No ser jubilado(a) <br>•&nbsp;&nbsp;&nbsp; No haber renunciado voluntariamente  \"\n" +
                "    }]}"

        Mockito.`when`(sharedPreferences.getString(Constants.CALCULATOR_RECOMMEND, "")).thenReturn(json)
        val result = Json.parse(ResultCalculatorWrapper.serializer(), json)


        calculatorViewModel.loadResultStaticFromSharePreferences()

        assertThat(calculatorViewModel.resultCalculatorRecommend,equalTo(result.results))
    }

    @Test
    fun `should get all categories`() {
        val routeResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val categoriesLiveData = MediatorLiveData<List<QueryingCategory>>()
        val categories = mutableListOf<QueryingCategory>()
        categories.add(
            MockObjects.mockQueryingCategory()
        )
        categoriesLiveData.value = categories
        Mockito.`when`(mockCategoryWithRelationsRepository.getAllCategories()).thenReturn(categoriesLiveData)

        val result = routeResultViewModel.retrieveAllCategories()

        verify(mockCategoryWithRelationsRepository).getAllCategories()
        assertThat(result,
            CoreMatchers.`is`(categoriesLiveData as LiveData<List<QueryingCategory>>)
        )
    }

    @Test
    fun `should call mapper as many times as querying categories exists`() {
        val queryingCategories = listOf(
            MockObjects.mockQueryingCategory(),
            MockObjects.mockQueryingCategory()
        )
        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        Mockito.`when`(mockCategoryMapper.mapFromModel(any())).thenReturn(Category(1, ""))

        finiquitoResultViewModel.mapCategories(queryingCategories)

        verify(
            mockCategoryMapper,
            times(queryingCategories.size)
        ).mapFromModel(any())
        assertThat(finiquitoResultViewModel.categories.size, CoreMatchers.`is`(queryingCategories.size))
    }

    @Test
    fun `should get a category when category id exists`() {
        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val categoryId = 1
        val category = Category(categoryId, "dummy")
        val queryingCategories = listOf(MockObjects.mockQueryingCategory())
        Mockito.`when`(mockCategoryMapper.mapFromModel(any())).thenReturn(category)
        finiquitoResultViewModel.mapCategories(queryingCategories)

        Mockito.`when`(mockCategoryMapper.mapFromModel(any())).thenReturn(category)

        val resultCategory = finiquitoResultViewModel.getCategoryById(categoryId)

        assertThat(resultCategory, CoreMatchers.`is`(category))
    }

    @Test
    fun `should getDecimoTercero mensualizado`(){
        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val idOption = CalculatorFiniquitoResultsViewModel.MENSUAL
        val startDate = "2018-01-01"
        val endDate= "2019-01-15"
        val salary = 500.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val decimoTercero = 20.83.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result =finiquitoResultViewModel.getDecimoTercero(idOption,startDate,endDate,salary)

        assertThat(decimoTercero,equalTo(result) )
    }

    @Test
    fun `should getDecimoTercero acumulado`(){
        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val idOption = 2
        val startDate = "2018-01-01"
        val endDate= "2019-01-15"
        val salary = 500.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val decimoTercero = 61.11.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result =finiquitoResultViewModel.getDecimoTercero(idOption,startDate,endDate,salary)

        assertThat(decimoTercero,equalTo(result) )
    }

    @Test
    fun `should getDecimoCuarto mensualizado`(){
        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val json = "{\n" +
                "  \"version\": 1,\n" +
                "  \"sbu\": 400,\n" +
                "  \"percentage_iess_afiliado\": 0.0945,\n" +
                "  \"percentage_fondos_reserva\": 0.0833,\n" +
                "  \"hours_complete_time\": 40\n" +
                "}"

        Mockito.`when`(sharedPreferences.getString(Constants.CONSTANTS, "")).thenReturn(json)

        val idOption = CalculatorFiniquitoResultsViewModel.MENSUAL
        val startDate = "2018-01-01"
        val endDate= "2019-01-15"
        val idArea = 1
        val hoursWorked = 40

        val decimoCuarto = 16.67.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result =finiquitoResultViewModel.getDecimoCuarto(idOption,startDate,endDate,idArea,hoursWorked)

        assertThat(decimoCuarto,equalTo(result) )
    }

    @Test
    fun `should getDecimoCuarto acumulado`(){
        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val json = "{\n" +
                "  \"version\": 1,\n" +
                "  \"sbu\": 400,\n" +
                "  \"percentage_iess_afiliado\": 0.0945,\n" +
                "  \"percentage_fondos_reserva\": 0.0833,\n" +
                "  \"hours_complete_time\": 40\n" +
                "}"

        Mockito.`when`(sharedPreferences.getString(Constants.CONSTANTS, "")).thenReturn(json)

        val idOption = 2
        val startDate = "2018-01-01"
        val endDate= "2019-01-15"
        val idArea = 1
        val hoursWorked = 30

        val decimoCuarto = 261.67.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result =finiquitoResultViewModel.getDecimoCuarto(idOption,startDate,endDate,idArea,hoursWorked)

        assertThat(decimoCuarto,equalTo(result) )
    }

    @Test
    fun `should fondos de reserva`(){
        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val json = "{\n" +
                "  \"version\": 1,\n" +
                "  \"sbu\": 400,\n" +
                "  \"percentage_iess_afiliado\": 0.0945,\n" +
                "  \"percentage_fondos_reserva\": 0.0833,\n" +
                "  \"hours_complete_time\": 40\n" +
                "}"

        Mockito.`when`(sharedPreferences.getString(Constants.CONSTANTS, "")).thenReturn(json)

        val idOption = CalculatorFiniquitoResultsViewModel.MENSUAL
        val startDate = "2018-01-01"
        val endDate= "2019-01-15"
        val salary = 500.toBigDecimal()

        val fondosReserva = 20.83.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result =finiquitoResultViewModel.getFondosReserva(idOption,startDate,endDate,salary)

        assertThat(result,equalTo(fondosReserva) )
    }
    @Test
    fun `should get salary last month`(){
        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val startDate = "2018-01-01"
        val endDate= "2019-01-15"
        val salary = 500.toBigDecimal()

        val salaryMonth = 250.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result =finiquitoResultViewModel.getSalaryLastMonth(salary,endDate, startDate)

        assertThat(result,equalTo(salaryMonth) )
    }

    @Test
    fun `should get vacation not taken`(){
        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val startDate = "2018-01-01"
        val endDate= "2019-01-15"
        val bornDate= "1994-01-13"
        val salary = 500.toDouble()
        val daysTaken =5

        val vacationPay = 166.67.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val result =finiquitoResultViewModel.getVacationsNotTaken(daysTaken,startDate, endDate, bornDate, salary)

        assertThat(result,equalTo(vacationPay) )
    }

    @Test
    fun `should get indemnizacion`(){
        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val HINT_EMBARAZO = "R3P1R3"
        val salary = 500.toBigDecimal()
        val indemnizacionPay = 6000.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =finiquitoResultViewModel.getIndemnizacion(HINT_EMBARAZO, salary )

        assertThat(result,equalTo(indemnizacionPay) )
    }

    @Test
    fun `should get causal value`(){
        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val EMPLOYEE_DEAD_HINT = "R3P2R3"
        val salary = 500.toBigDecimal()
        val startDate = "2018-01-01"
        val endDate= "2019-01-15"
        val causalPay = 0.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

        val result =finiquitoResultViewModel.getDesahucio(EMPLOYEE_DEAD_HINT, salary, startDate, endDate )

        assertThat(result,equalTo(causalPay) )
    }

    @Test
    fun `should get faults`(){
        val faultsList = ("F1 F2 F3 F4 F5")
        Mockito.`when`(sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_CALCULATOR, "")).thenReturn(faultsList)

        val finiquitoResultViewModel = CalculatorFiniquitoResultsViewModel(
            sharedPreferences,
            mockCategoryWithRelationsRepository, mockCategoryMapper
        )
        val json = "{\n" +
                "  \"version\": 1,\n" +
                "  \"sbu\": 400,\n" +
                "  \"percentage_iess_afiliado\": 0.0945,\n" +
                "  \"percentage_fondos_reserva\": 0.0833,\n" +
                "  \"hours_complete_time\": 40\n" +
                "}"

        Mockito.`when`(sharedPreferences.getString(Constants.CONSTANTS, "")).thenReturn(json)

        val startDate = "2018-01-14"
        val endDate= "2019-01-15"
        val bornDate= "2002-01-13"
        val salary = "200"
        val hours = 40

        val faults = mutableListOf("F1", "F2", "F3", "F4", "F5")

        val result =finiquitoResultViewModel.getFaultsApplicable(salary,hours, bornDate ,startDate, endDate)

        assertThat(result,equalTo(faults) )
    }
}