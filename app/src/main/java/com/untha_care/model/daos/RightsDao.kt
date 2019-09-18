package com.untha_care.model.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.untha_care.model.models.Category

@Dao
interface RightsDao : BaseDao<Category> {

    @Query("SELECT * FROM Category ORDER BY id DESC")
    fun getAll(): LiveData<List<Category>>

}