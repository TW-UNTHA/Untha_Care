package com.untha.model.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.untha.model.models.QueryingCategory

@Dao
interface CategoryWithRelationsDao {

    @Query("SELECT * FROM CategoryModel")
    fun getAllCategories(): LiveData<List<QueryingCategory>>

    @Query("SELECT * FROM CategoryModel WHERE parent_id=2 ORDER BY id ASC")
    fun getAllRightsCategoriess(): LiveData<List<QueryingCategory>>

    @Query("SELECT * FROM CategoryModel WHERE parent_id is null")
    fun findMain(): LiveData<List<QueryingCategory>>
}
