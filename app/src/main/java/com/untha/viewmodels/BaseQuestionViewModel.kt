package com.untha.viewmodels

import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.untha.model.transactionalmodels.Route
import com.untha.model.transactionalmodels.RouteQuestion
import com.untha.utils.Constants

open class BaseQuestionViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    var question: RouteQuestion? = null
        private set

    fun saveAnswerOption(fault: String?, nameRoute: String) {
        val faultAnswers = loadFaultAnswersFromSharedPreferences(nameRoute) ?: ""
        sharedPreferences.edit()
            .putString(
                nameRoute,
                "$faultAnswers $fault"

            ).apply()
    }

    private fun loadFaultAnswersFromSharedPreferences(nameRoute: String): String? {
        return sharedPreferences.getString(nameRoute, "")
    }

    fun loadQuestion(goTo: Int?, route: Route) {
        question = route.questions.firstOrNull { it.id == goTo }
    }

    fun isSingleQuestion(type: String?): Boolean {
        if(type == Constants.SINGLE_QUESTION) return true
        return false
    }

    fun isLabourRoute(bundle: Bundle): Boolean{
        return when {
            bundle.containsKey(Constants.ROUTE_LABOUR) -> {
                true
            }
            else -> false
        }
    }

    fun loadRoute(isLabourRoute: Boolean, bundle: Bundle): Route{
        return if (isLabourRoute){
            bundle.get(Constants.ROUTE_LABOUR) as Route
        }else{
            bundle.get(Constants.ROUTE_VIOLENCE) as Route
        }
    }

    fun calculatePercentQuestionsAnswered(numberOfQuestionAnswered: Int, numberOfRemainingQuestions: Int): Int {
        val totalQuestions = numberOfQuestionAnswered + numberOfRemainingQuestions
        val percentajeQuestionsAnswered = numberOfQuestionAnswered * Constants.TOTAL_PROGRESS_BAR/totalQuestions
        return percentajeQuestionsAnswered
    }
}

