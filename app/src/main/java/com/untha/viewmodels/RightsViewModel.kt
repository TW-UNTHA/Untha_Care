package com.untha.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.transactionalmodels.Category

class RightsViewModel(
    private val categoryWithRelationsRepository: CategoryWithRelationsRepository,
    private val mapper: CategoryMapper
) :
    ViewModel() {

    fun getRightsCategoryModels(): LiveData<List<QueryingCategory>> {
        return categoryWithRelationsRepository.getAllRightsCategories()
    }

    fun getRightCategories(queryingCategories: List<QueryingCategory>): List<Category> {
        return queryingCategories.map { queryingCategory ->
            mapper.mapFromModel(queryingCategory)
        }
    }
}
