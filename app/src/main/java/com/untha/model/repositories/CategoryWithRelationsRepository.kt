package com.untha.model.repositories

import androidx.lifecycle.LiveData
import com.untha.model.database.ApplicationDatabase
import com.untha.model.models.QueryingCategory

class CategoryWithRelationsRepository(database: ApplicationDatabase) {

    private val categoryWithRelationsDao = database.categoryWithRelationsDao()

    fun getAllCategories(): LiveData<List<QueryingCategory>> {
        return categoryWithRelationsDao.getAllCategories()
    }
}
