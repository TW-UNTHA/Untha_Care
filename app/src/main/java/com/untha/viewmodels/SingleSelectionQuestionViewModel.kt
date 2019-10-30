package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.untha.model.transactionalmodels.RouteQuestion
import com.untha.utils.Constants

class SingleSelectionQuestionViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    fun loadQuestionLabourRoute(goTo:Int , questions: List<RouteQuestion>): RouteQuestion? {
        return questions.last { it.id == goTo }
    }

    fun saveAnswerOption(fault: String){
        val faultAnswers = loadAnswerOptionFromSharedPreferences()?:""
        sharedPreferences.edit()
            .putString(
                Constants.FAULT_ANSWER,
                "$faultAnswers $fault"

            ).apply()
    }

    fun loadAnswerOptionFromSharedPreferences(): String? {
        return sharedPreferences.getString(Constants.FAULT_ANSWER, "")
    }

}

