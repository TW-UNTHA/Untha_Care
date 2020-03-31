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
import com.untha.utils.ConstantsCalculators.SEVENTEEN_AGE
import com.untha.utils.ConstantsCalculators.SIXTEEN_AGE
import com.untha.utils.ConstantsCalculators.TRIAL_PERIOD
import com.untha.utils.ConstantsCalculators.WEEKLY_HOURS_MAXIMUM_LEGAL_MINOR
import com.untha.utils.ConstantsValues
import com.untha.utils.calculateNumberOfDayBetween
import com.untha.utils.getAge
import com.untha.utils.salaryForDaysWorked
import com.untha.utils.stringToCalendar
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
        const val PAY_NOT_COMPLETE = "F1"
        const val LEGAL_MINOR_AND_OVER_HOURS_WORKED = "F2"
        const val DESPIDO_INTEMPESTIVO = "F4"
        const val DISABILITY = "F5"
        const val OVER_HOURS_WORKED = "F6"
        const val PREGNANT_AND_TRIAL_PERIOD = "F3"
    }

    var resultCalculatorFaults: List<RouteResult>? = null
    var resultCalculatorRecommend: List<ResultCalculator>? = null

    private var calculatorDecimosService = CalculatorDecimosService(sharedPreferences)
    private var calculatorIESSService = CalculatorIESSService(sharedPreferences)
    private var calculatorVacationsService = CalculatorVacacionesService()
    private var calculatorFiniquitoCompensationService = CalculatorFiniquitoCompensationService()
    private var calculatorFiniquitoCausalesService = CalculatorFiniquitoCausalesService()
    val constantsValues = ConstantsValues(sharedPreferences!!)

    val resultFaultAnswerRouteCalculator =
        sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_CALCULATOR, "")
    var resultsSelected = resultFaultAnswerRouteCalculator?.split(" ") ?: listOf()

    val resultFaultAnswerRouteCalculatorHint =
        sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_CALCULATOR_HINT, "")
    var hintsSelected = resultFaultAnswerRouteCalculatorHint?.split(" ") ?: listOf()

    var categories: MutableList<Category> = mutableListOf()
        private set

    fun loadResultDynamicFromSharePreferences() {
        val jsonResultDynamic = sharedPreferences.getString(Constants.CALCULATOR_ROUTE_RESULT, "")
        jsonResultDynamic?.let {
            val result = Json.parse(ResultWrapper.serializer(), it)
            resultCalculatorFaults = result.results
        }
    }

    fun loadResultStaticFromSharePreferences() {
        val jsonResultDynamic = sharedPreferences.getString(Constants.CALCULATOR_RECOMMEND, "")
        jsonResultDynamic?.let {
            val result = Json.parse(ResultCalculatorWrapper.serializer(), it)
            resultCalculatorRecommend = result.results
        }
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

    fun getIndemnizacion(hint: String, salary: BigDecimal): BigDecimal {
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

    fun getFaultsApplicable(
        salary: String,
        hours: Int,
        bornDate: String,
        startDate: String,
        endDate: String
    ): MutableList<String> {
        val answersApplicable = mutableListOf<String>()

        if (salary.toBigDecimal() < constantsValues.getSBU().toBigDecimal()
            && hours >= constantsValues.getCompleteTimeHours()
        ) {
            answersApplicable.add(PAY_NOT_COMPLETE)
        }
        if (hours > WEEKLY_HOURS_MAXIMUM_LEGAL_MINOR && (getAge(
                bornDate,
                startDate
            ) in SIXTEEN_AGE..SEVENTEEN_AGE)
        ) {
            answersApplicable.add(LEGAL_MINOR_AND_OVER_HOURS_WORKED)

        }

        if (resultsSelected!!.contains(PREGNANT_AND_TRIAL_PERIOD)
            && (calculateNumberOfDayBetween(
                stringToCalendar(startDate),
                stringToCalendar(endDate)
            ) > TRIAL_PERIOD)
        ) {
            answersApplicable.add(PREGNANT_AND_TRIAL_PERIOD)

        }

        if (resultsSelected!!.contains(DESPIDO_INTEMPESTIVO)) {
            answersApplicable.add(DESPIDO_INTEMPESTIVO)
        }

        if (resultsSelected!!.contains(DISABILITY)) {
            answersApplicable.add(DISABILITY)
        }
        if (hours > constantsValues.getCompleteTimeHours()) {
            answersApplicable.add(OVER_HOURS_WORKED)
        }
        return answersApplicable
    }

}
