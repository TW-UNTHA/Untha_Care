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
        if (type == Constants.SINGLE_QUESTION) return true
        return false
    }

    fun getTypeRoute(bundle: Bundle): String {
        if (bundle.containsKey(Constants.ROUTE_LABOUR)) {
            return Constants.ROUTE_LABOUR
        }
        if (bundle.containsKey(Constants.ROUTE_VIOLENCE)) {
            return Constants.ROUTE_VIOLENCE
        }
        return Constants.ROUTE_CALCULATOR
    }

    fun loadRoute(typeRoute: String, bundle: Bundle): Route {
        println(bundle)
        return bundle.get(typeRoute) as Route
    }

    fun calculatePercentQuestionsAnswered(
        numberOfQuestionAnswered: Int,
        numberOfRemainingQuestions: Int,
        typeRoute: String
    ): Int {
        var questionsMores = 0
        if (typeRoute == Constants.ROUTE_CALCULATOR) {
            questionsMores = 2

        }

        val totalQuestions = numberOfQuestionAnswered + numberOfRemainingQuestions + questionsMores
        return numberOfQuestionAnswered * Constants.TOTAL_PROGRESS_BAR / totalQuestions
    }

    fun saveCompleteRouteResult(typeRoute: String) {
        when (typeRoute) {
            Constants.ROUTE_LABOUR -> {
                loadFaultAnswersFromSharedPreferences(Constants.FAULT_ANSWER_ROUTE_LABOUR)?.let {
                    sharedPreferences.edit().putString(Constants.COMPLETE_LABOUR_ROUTE, it).apply()
                }
            }

            Constants.ROUTE_VIOLENCE -> {
                loadFaultAnswersFromSharedPreferences(Constants.FAULT_ANSWER_ROUTE_VIOLENCE)?.let {
                    sharedPreferences.edit().putString(Constants.COMPLETE_VIOLENCE_ROUTE, it)
                        .apply()
                }
            }
        }
    }
}


