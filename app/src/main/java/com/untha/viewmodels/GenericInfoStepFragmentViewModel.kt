package com.untha.viewmodels

import androidx.lifecycle.ViewModel
import com.untha.model.transactionalmodels.Category

class GenericInfoStepFragmentViewModel : ViewModel() {

    var categoriesNextStep: ArrayList<Category> = arrayListOf()
        private set


    fun getNextSteps(category: Category, categories: List<Category>) {
        val nextStepIds = category.information?.map { info -> info.nextStep } ?: listOf()
        val nextStepCategories = categories.filter { it.id in nextStepIds }
        categoriesNextStep.addAll(nextStepCategories)
    }
}
