package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.ResultCalculator
import com.untha.model.transactionalmodels.ResultCalculatorWrapper
import com.untha.model.transactionalmodels.ResultWrapper
import com.untha.model.transactionalmodels.RouteResult
import com.untha.utils.Constants
import kotlinx.serialization.json.Json

class CalculatorFiniquitoResultsViewModel(
    private val sharedPreferences: SharedPreferences,
    private val categoryWithRelationsRepository: CategoryWithRelationsRepository,
    private val mapper: CategoryMapper
) : ViewModel() {

    private lateinit var resultDynamic: List<RouteResult>
    private lateinit var resultCalculatorStatic: List<ResultCalculator>

    var categories: MutableList<Category> = mutableListOf()
        private set

    fun loadResultDynamicFromSharePreferences() {
        val jsonResultDynamic = sharedPreferences.getString(Constants.CALCULATOR_ROUTE_RESULT, "")
        jsonResultDynamic?.let {
            val result = Json.parse(ResultWrapper.serializer(), it)
            resultDynamic = result.results
        }
    }

    fun loadResultStaticFromSharePreferences() {
        val jsonResultDynamic = sharedPreferences.getString(Constants.CALCULATOR_RECOMMEND, "")
        jsonResultDynamic?.let {
            val result = Json.parse(ResultCalculatorWrapper.serializer(), it)
            resultCalculatorStatic = result.resultCalculators
        }
    }

    fun answerSelectedCalculatorRoute(): List<String> {
        val results = sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_CALCULATOR, "")
        return results?.split(" ") ?: listOf()
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
}
