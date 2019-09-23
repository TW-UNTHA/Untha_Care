package com.untha.model.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.untha.model.models.Category

@Dao
interface RightsDao : BaseDao<Category> {

    @Query("SELECT * FROM Category WHERE type='derechos' ORDER BY id DESC")
    fun getAll(): LiveData<List<Category>>

}