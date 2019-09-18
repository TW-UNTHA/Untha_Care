package com.untha_care.viewmodels.listCategoryDerechos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.untha_care.model.models.Category
import com.untha_care.model.repositories.CategoryRepository

class CategoryListViewModel(private val categoryRepository: CategoryRepository): ViewModel(){
    private val categoryList = MediatorLiveData<List<Category>>()

    init {
        getAllCategories()
    }

    fun getCategoryList(): LiveData<List<Category>> {
        return categoryList
    }


    private fun getAllCategories() {
        categoryList.addSource(categoryRepository.getAll()) { categories ->
            categoryList.postValue(categories)
        }
    }

}
