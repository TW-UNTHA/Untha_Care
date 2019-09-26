package com.untha.model.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.untha.model.models.QueryingCategory
import androidx.room.Transaction

@Dao
abstract class CategoryWithRelationsDao {

    @Transaction
    @Query("SELECT * FROM CategoryModel")
    abstract fun getAllCategories(): LiveData<List<QueryingCategory>>

    @Transaction
    @Query("SELECT * FROM CategoryModel WHERE parent_id=2 ORDER BY id ASC")
    abstract fun getAllRightsCategoriess(): LiveData<List<QueryingCategory>>

}