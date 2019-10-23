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

    var categories: ArrayList<Category> = arrayListOf()
        private set

    fun findMainCategories(): LiveData<List<QueryingCategory>> {
        return categoryWithRelationsRepository.findMainCategories()
    }

    fun getCategories(categoriesQuerying: List<QueryingCategory>) {
        categories.clear()
        categories.addAll(categoriesQuerying.map {
            categoryMapper.mapFromModel(it)
        })
    }

    fun getCategoryRoutes(): ArrayList<Category> {
        return categories.filter { it.isRoute } as ArrayList
    }

}
