package com.untha.model.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.untha.database.ApplicationDatabase
import com.untha.model.daos.RightsDao
import com.untha.model.models.Category

class RightsRepository(application: Application) {

    private var rightsDao: RightsDao

    init {
        val categoryDatabase = ApplicationDatabase.getInstance(application)
        rightsDao = categoryDatabase.categoryDao()
    }

    fun getAll(): LiveData<List<Category>> {
        return rightsDao.getAll()
    }



}


