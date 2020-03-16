package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untha.applicationservices.CalculatorDecimosService
import com.untha.applicationservices.CalculatorFiniquitoCausalesService
import com.untha.applicationservices.CalculatorFiniquitoCompensationService
import com.untha.applicationservices.CalculatorIESSService
import com.untha.applicationservices.CalculatorVacacionesService
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.ResultCalculator
import com.untha.model.transactionalmodels.ResultCalculatorWrapper
import com.untha.model.transactionalmodels.ResultWrapper
import com.untha.model.transactionalmodels.RouteResult
import com.untha.utils.Constants
import com.untha.utils.salaryForDaysWorked
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorFiniquitoResultsViewModel(
    private val sharedPreferences: SharedPreferences,
    private val categoryWithRelationsRepository: CategoryWithRelationsRepository,
    private val mapper: CategoryMapper
) : ViewModel() {
    companion object {
        const val MENSUAL = 1
    }
    var resultCalculatorRoute: List<RouteResult>? = null
    var resultCalculatorRecommend: List<ResultCalculator>? = null
    var resultsSelected: List<String>? = null

    private var calculatorDecimosService = CalculatorDecimosService()
    private var calculatorIESSService = CalculatorIESSService()
    private var calculatorVacationsService = CalculatorVacacionesService()
    private var calculatorFiniquitoCompensationService = CalculatorFiniquitoCompensationService()
    private var calculatorFiniquitoCausalesService = CalculatorFiniquitoCausalesService()

    var categories: MutableList<Category> = mutableListOf()
        private set

    fun loadResultDynamicFromSharePreferences() {
        val jsonResultDynamic = sharedPreferences.getString(Constants.CALCULATOR_ROUTE_RESULT, "")
        jsonResultDynamic?.let {
            val result = Json.parse(ResultWrapper.serializer(), it)
            resultCalculatorRoute = result.results
        }
    }

    fun loadResultStaticFromSharePreferences() {
        val jsonResultDynamic = sharedPreferences.getString(Constants.CALCULATOR_RECOMMEND, "")
        jsonResultDynamic?.let {
            val result = Json.parse(ResultCalculatorWrapper.serializer(), it)
            resultCalculatorRecommend = result.results
        }
    }

    fun answerSelectedCalculatorRoute() {
        val results = sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_CALCULATOR, "")
        resultsSelected = results?.split(" ") ?: listOf()
    }

    fun retrieveAllCategories(): LiveData<List<QueryingCategory>> {
        return categoryWithRelationsRepository.getAllCategories()
    }

    fun mapCategories(queryingCategories: List<QueryingCategory>) {
        queryingCategories.map { queryingCategory ->
            categories.add(mapper.mapFromModel(queryingCategory))
        }
    }

    fun getCategoryById(id: Int): Category? {
        return categories.firstOrNull { it.id == id }
    }

    fun getDecimoTercero(
        idOption: Int,
        startDate: String,
        endDate: String,
        salary: BigDecimal
    ): BigDecimal {
        if (idOption == MENSUAL) {
            return calculatorDecimosService.getDecimoTercerSueldoFiniquitoMensualizado(
                startDate,
                endDate,
                salary
            )
        }
        return calculatorDecimosService.getDecimoTercerSueldoAcumulado(salary, startDate, endDate)

    }

    fun getDecimoCuarto(
        idOption: Int,
        startDate: String,
        endDate: String,
        idArea: Int,
        hoursWorked: Int
    ): BigDecimal {
        if (idOption == MENSUAL) {
            return calculatorDecimosService.getDecimoCuartoSueldoMensualizadoFiniquito(
                startDate,
                endDate,
                hoursWorked
            )
        }
        return calculatorDecimosService.getDecimoCuartoAcumulado(
            startDate,
            endDate,
            idArea,
            hoursWorked
        )

    }

    fun getFondosReserva(
        idOption: Int,
        startDate: String,
        endDate: String,
        salary: BigDecimal
    ): BigDecimal {
        if (idOption == MENSUAL) {
            return calculatorIESSService.getFondoReservaMensualizadoFiniquito(
                startDate,
                endDate,
                salary
            )
        }
        return 0.toBigDecimal().setScale(2, RoundingMode.HALF_UP)

    }

    fun getSalaryLastMonth(salary: BigDecimal, endDate: String, startDate: String): BigDecimal {
        return salaryForDaysWorked(startDate, endDate, salary)
    }

    fun getVacationsNotTaken(
        daysTaken: Int,
        startDate: String,
        endDate: String,
        bornDate: String,
        salary: Double
    ): BigDecimal {
        return calculatorVacationsService.getTotalCostPeriod(
            startDate,
            endDate,
            bornDate,
            salary,
            daysTaken
        )
    }

    fun getDesahucio(hint: String, salary: BigDecimal): BigDecimal {
        return calculatorFiniquitoCompensationService.getCompensation(hint, salary)
    }

    fun getDesahucio(
        hint: String,
        salary: BigDecimal,
        startDate: String,
        endDate: String
    ): BigDecimal {
        return calculatorFiniquitoCausalesService.getCausalRetribution(
            hint,
            salary,
            startDate,
            endDate
        )
    }

}
