package com.untha.viewmodels

import androidx.lifecycle.ViewModel
import com.untha.model.transactionalmodels.Category

class GenericInfoStepFragmentViewModel : ViewModel() {

    var categoriesNextStep: ArrayList<Category> = arrayListOf()
        private set


    fun getNextSteps(category: Category, categories: List<Category>) {

        val nextStepCategories = categories.map { actualCategory ->
            actualCategory.id
        }
        val nextSteps1 = category.information?.map { info -> info.nextStep }

        var result: List<Int> = listOf()
        if (nextSteps1 != null) {
            result = nextStepCategories.intersect(nextSteps1).filterNotNull()
        }

        val nextStepsCategories2 = categories.filter { it.id in result }

        categoriesNextStep.addAll(nextStepsCategories2)
    }
}
