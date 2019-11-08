package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.untha.model.transactionalmodels.Route
import com.untha.model.transactionalmodels.RouteQuestion
import com.untha.utils.Constants

open class BaseViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    var question: RouteQuestion? = null
        private set

    fun saveAnswerOption(fault: String?) {
        val faultAnswers = loadFaultAnswersFromSharedPreferences() ?: ""
        sharedPreferences.edit()
            .putString(
                Constants.FAULT_ANSWER,
                "$faultAnswers $fault"

            ).apply()
    }

    private fun loadFaultAnswersFromSharedPreferences(): String? {
        return sharedPreferences.getString(Constants.FAULT_ANSWER, "")
    }

    fun loadQuestion(goTo: Int?, route: Route) {
        question = route.questions.firstOrNull { it.id == goTo }
    }

    fun isSingleQuestion(type: String?): Boolean {
        if(type == "SingleOption"){
           return true
        }
        return false
    }

}

