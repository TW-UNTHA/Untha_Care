package com.untha_care.viewmodels

import androidx.lifecycle.ViewModel
import com.untha_care.model.repositories.CategoryRepository
import com.untha_care.model.services.LawsAndRightsService

class ExampleViewModel(
    private val category: CategoryRepository,
    private val lawsAndRightsService: LawsAndRightsService
) : ViewModel() {


    fun getCategory() {
        category.getAll()
    }

}