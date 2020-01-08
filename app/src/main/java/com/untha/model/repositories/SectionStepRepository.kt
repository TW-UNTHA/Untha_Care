package com.untha.model.repositories

import com.untha.model.daos.SectionStepDao
import com.untha.model.database.ApplicationDatabase
import com.untha.model.models.SectionStepModel

class SectionStepRepository(database: ApplicationDatabase) {

    private var sectionStepDao: SectionStepDao = database.sectionStepDao()

    fun insert(sectionStepModels: List<SectionStepModel>): List<Long> {
        return sectionStepDao.insert(sectionStepModels)
    }
}
