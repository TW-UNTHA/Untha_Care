package com.untha.viewmodels

import android.content.SharedPreferences
import com.untha.model.transactionalmodels.RouteQuestion

class SingleSelectionQuestionViewModel(
     sharedPreferences: SharedPreferences
) :  BaseViewModel(sharedPreferences){

    fun loadQuestionLabourRoute(goTo:Int? , questions: List<RouteQuestion>): RouteQuestion? {
        return questions.last { it.id == goTo }
    }
}

