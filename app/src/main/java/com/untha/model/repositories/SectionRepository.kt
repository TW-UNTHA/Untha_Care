package com.untha.model.repositories

import com.untha.model.database.ApplicationDatabase
import com.untha.model.daos.SectionModelDao
import com.untha.model.models.SectionModel

class SectionRepository(database: ApplicationDatabase) {

    private var sectionDao: SectionModelDao = database.sectionDao()

    fun insert(sectionModels: List<SectionModel>) {
        sectionDao.insert(sectionModels)
    }
}
