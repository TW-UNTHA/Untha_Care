package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

class CategoryViewModel(
        private val categoryWithRelationsRepository: CategoryWithRelationsRepository,
        private val categoryMapper: CategoryMapper,
        private val sharedPreferences: SharedPreferences
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
        return categories.filter { it.type == "route" } as ArrayList
    }

    fun getCategoryCalculators(): ArrayList<Category> {
        return categories.filter { it.type == "calculator" } as ArrayList
    }

    fun saveCategoryRoutesInSharedPreferences(categories: List<Category>) {
        sharedPreferences.edit()
            .putString(
                Constants.CATEGORIES_ROUTES,
                Json.stringify(Category.serializer().list, categories)

            ).apply()
    }

    fun saveCategoryCalculatorsInSharedPreferences(categories: List<Category>) {
        sharedPreferences.edit()
            .putString(
                Constants.CATEGORIES_CALCULATORS,
                Json.stringify(Category.serializer().list, categories)

            ).apply()
    }

    fun loadCategoriesRoutesFromSharedPreferences(): ArrayList<Category>? {
        val jsonCategoriesRoutes = sharedPreferences.getString(Constants.CATEGORIES_ROUTES, null)
        return jsonCategoriesRoutes?.let {
            Json.parse(
                Category.serializer().list,
                it
            )
        } as ArrayList<Category>
    }

}
