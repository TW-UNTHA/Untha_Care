package com.untha.model.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.untha.model.models.QueryingCategory

@Dao
interface CategoryWithRelationsDao {
    @Query("SELECT * FROM CategoryModel")
    fun getAllCategories(): LiveData<List<QueryingCategory>>
}
