package com.untha.model.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.untha.model.database.ApplicationDatabase
import com.untha.model.daos.CategoryDao
import com.untha.model.daos.RightsDao
import com.untha.model.models.Category
import com.untha.model.models.CategoryModel

class CategoryRepository(database: ApplicationDatabase) {

    private var categoryDao: RightsDao

    init {
        val categoryDatabase = ApplicationDatabase.getInstance(application)
        categoryDao = categoryDatabase.categoryDao()
    }

    fun insert(categoryModels: List<CategoryModel>) {
        categoryDao.insert(categoryModels)
    }

    fun getAll(): LiveData<List<Category>> {
        return categoryDao.getAll()
    }
}