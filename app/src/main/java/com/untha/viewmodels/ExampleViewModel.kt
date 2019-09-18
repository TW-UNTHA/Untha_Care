package com.untha.viewmodels

import androidx.lifecycle.ViewModel
import com.untha.model.repositories.CategoryRepository
import com.untha.model.services.LawsAndRightsService

class ExampleViewModel(
    private val category: CategoryRepository,
    private val lawsAndRightsService: LawsAndRightsService
) : ViewModel() {

    fun getCategory() {
        category.getAll()
    }
}
