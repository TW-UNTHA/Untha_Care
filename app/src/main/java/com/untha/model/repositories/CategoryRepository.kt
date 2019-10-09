package com.untha.model.repositories

import com.untha.model.daos.CategoryDao
import com.untha.model.database.ApplicationDatabase
import com.untha.model.models.CategoryModel

class CategoryRepository(database: ApplicationDatabase) {

    private var categoryDao: CategoryDao = database.categoryDao()


    fun insert(categoryModels: List<CategoryModel>) {
        categoryDao.insert(categoryModels)
    }

}

