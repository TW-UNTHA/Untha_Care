package com.untha.model.repositories

import androidx.lifecycle.LiveData
import com.untha.database.ApplicationDatabase
import com.untha.model.daos.CategoryDao
import com.untha.model.models.Category

class CategoryRepository(private val database: ApplicationDatabase) {

    private var categoryDao: CategoryDao = database.categoryDao()

    fun getAll(): LiveData<List<Category>> {
        return categoryDao.getAll()
    }
}
