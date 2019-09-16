package com.untha_care.model.repositories

import androidx.lifecycle.LiveData
import com.untha_care.database.ApplicationDatabase
import com.untha_care.model.daos.CategoryDao
import com.untha_care.model.models.Category

class CategoryRepository(private val database: ApplicationDatabase) {

    private var categoryDao: CategoryDao = database.categoryDao()

    fun getAll(): LiveData<List<Category>> {
        return categoryDao.getAll()
    }
}