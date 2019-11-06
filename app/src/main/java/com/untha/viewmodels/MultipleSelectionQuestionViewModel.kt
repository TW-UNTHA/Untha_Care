package com.untha.viewmodels

import android.content.SharedPreferences
import com.untha.model.transactionalmodels.Route
import com.untha.model.transactionalmodels.RouteQuestion

class MultipleSelectionQuestionViewModel(
    sharedPreferences: SharedPreferences
) : BaseViewModel(sharedPreferences) {

    var question: RouteQuestion? = null
        private set

    fun loadQuestion(goTo: Int?, route: Route) {
        question = route.questions.firstOrNull { it.id == goTo }
    }

    fun getFaultForQuestion(isNoneOfAbove: Boolean) {
        return if (isNoneOfAbove) {
            saveAnswerOption(question?.options?.last()?.result ?: "")
        } else {
            saveAnswerOption(question?.options?.first()?.result ?: "")
        }
    }

    fun getHintForSelectedOption(isNoneOfAbove: Boolean): String? {
        return if (isNoneOfAbove) {
            question?.options?.last()?.hint
        } else {
            question?.options?.first()?.hint
        }
    }
}

