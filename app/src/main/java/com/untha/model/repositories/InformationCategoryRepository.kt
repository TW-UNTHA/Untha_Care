package com.untha.model.repositories

import com.untha.model.database.ApplicationDatabase
import com.untha.model.daos.CategoryInformationDao
import com.untha.model.models.CategoryInformationModel

class InformationCategoryRepository(database: ApplicationDatabase) {

    private var informationCategoryDao: CategoryInformationDao = database.categoryInformationDao()

    fun insert(categoryInformationModels: List<CategoryInformationModel>) {
        informationCategoryDao.insert(categoryInformationModels)
    }
}
