package com.untha.viewmodels

import androidx.lifecycle.ViewModel
import com.untha.model.transactionalmodels.RouteQuestion

class SingleSelectionQuestionViewModel : ViewModel() {
    lateinit var   routeQuestion : RouteQuestion

    fun loadQuestionLabourRoute(goTo:Int , questions: List<RouteQuestion>): RouteQuestion? {
        return questions.last { it.id == goTo }
    }
    fun loadAnswerOption(){




    }


}
