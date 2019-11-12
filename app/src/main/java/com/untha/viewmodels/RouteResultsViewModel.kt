package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.untha.model.mappers.CategoryMapper
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

    private fun getRouteResultsIds(): List<String> {
        val results = sharedPreferences.getString(Constants.FAULT_ANSWER, "")
        return results?.split(" ") ?: listOf()
    }

    fun retrieveRouteResults() {
        val answers = getRouteResultsIds()
        if (!answers.isNullOrEmpty()) {
            val result = sharedPreferences.getString(Constants.ROUTE_RESULT, "")
            if (!result.isNullOrEmpty()) {
                val resultWrapper = Json.parse(ResultWrapper.serializer(), result)
                val matchingRouteResults =
                    resultWrapper.routeResults.filter { routeResult -> routeResult.id in answers }
                routeResults = if (matchingRouteResults.isEmpty()) null else matchingRouteResults
            }
        }
    }

    fun retrieveAllCategories(owner: LifecycleOwner) {
        categoryWithRelationsRepository.getAllCategories()
            .observe(owner, Observer { queryingCategories ->
                queryingCategories.map { queryingCategory ->
                    categories.add(mapper.mapFromModel(queryingCategory))
                }
            })
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
}
