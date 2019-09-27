package com.untha.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.transactionalmodels.Category

class CategoryViewModel(
    private val categoryWithRelationsRepository: CategoryWithRelationsRepository,
    private val categoryMapper: CategoryMapper
) : ViewModel() {

    fun findMainCategories(): LiveData<List<QueryingCategory>> {
        return categoryWithRelationsRepository.findMainCategories()
    }

    fun getCategories(categoriesQuerying: List<QueryingCategory>): List<Category> {
        return categoriesQuerying.map { categoryMapper.mapFromModel(it) }
    }

}
