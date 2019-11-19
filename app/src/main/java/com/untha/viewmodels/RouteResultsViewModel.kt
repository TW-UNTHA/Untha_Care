package com.untha.viewmodels

import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.QuestionnaireRouteResult
import com.untha.model.transactionalmodels.QuestionnaireRouteResultWrapper
import com.untha.model.transactionalmodels.ResultWrapper
import com.untha.model.transactionalmodels.RouteResult
import com.untha.utils.Constants
import kotlinx.serialization.json.Json

class RouteResultsViewModel(
    private val sharedPreferences: SharedPreferences,
    private val categoryWithRelationsRepository: CategoryWithRelationsRepository,
    private val mapper: CategoryMapper
) : ViewModel() {

    var routeResults: List<RouteResult>? = null
        private set

    var categories: MutableList<Category> = mutableListOf()
        private set

    private lateinit var questionnaires: List<QuestionnaireRouteResult>

    private fun getLabourRouteResultsIds(): List<String> {
        val results = sharedPreferences.getString(Constants.COMPLETE_LABOUR_ROUTE, "")
        return results?.split(" ") ?: listOf()
    }

    fun retrieveRouteResults() {
        val answers = getLabourRouteResultsIds()
        if (!answers.isNullOrEmpty()) {
            val result = sharedPreferences.getString(Constants.ROUTE_RESULT, "")
            if (!result.isNullOrEmpty()) {
                val resultWrapper = Json.parse(ResultWrapper.serializer(), result)
                val matchingRouteResults =
                    resultWrapper.results.filter { routeResult -> routeResult.id in answers }
                routeResults = if (matchingRouteResults.isEmpty()) null else matchingRouteResults
            }
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

    fun loadQuestionnaire() {
        val jsonQuestionnaire = sharedPreferences.getString(Constants.QUESTIONNAIRE_ROUTE, "")
        jsonQuestionnaire?.let {
            val questionnaire = Json.parse(QuestionnaireRouteResultWrapper.serializer(), it)
            questionnaires = questionnaire.results
        }
    }

    fun getQuestionnairesByType(type: String): List<QuestionnaireRouteResult> {
        return questionnaires.filter { questionnaire -> questionnaire.type == type }
    }

    fun getRouteResultsByType(type: String): List<RouteResult>? {
        val filteredRouteResults = routeResults?.filter { it.type == type } ?: listOf()
        return if (filteredRouteResults.isEmpty()) null else filteredRouteResults
    }

    fun getQuestionnairesByTypeAndCode(
        type: String,
        code: String
    ): List<QuestionnaireRouteResult>? {
        val filteredResults =
            questionnaires.filter { questionnaire ->
                questionnaire.type == type &&
                        questionnaire.code == code
            }
        return if (filteredResults.isEmpty()) null else filteredResults
    }

    fun getHigherViolenceLevel(): String? {
        val violenceRouteResult =
            sharedPreferences.getString(Constants.COMPLETE_VIOLENCE_ROUTE, "")
        val results = violenceRouteResult?.split(" ")
        val high = results?.firstOrNull { it == "ALTO" }
        val medium = results?.firstOrNull { it == "MEDIO" }
        val low = results?.firstOrNull { it == "BAJO" }
        var result: String? = null
        when {
            high != null -> result = high
            medium != null -> result = medium
            low != null -> result = low
        }
        return result
    }

    fun isLabourRoute(bundle: Bundle): Boolean {
        return when {
            bundle.getBoolean(Constants.IS_LABOUR_ROUTE) -> {
                true
            }
            else -> false
        }
    }
}
