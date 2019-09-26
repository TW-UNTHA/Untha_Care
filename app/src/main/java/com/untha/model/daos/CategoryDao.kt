package com.untha.model.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.untha.model.models.CategoryModel

@Dao
interface CategoryDao : BaseDao<CategoryModel> {

    @Query("SELECT * FROM CategoryModel ORDER BY id DESC")
    fun getAll(): LiveData<List<CategoryModel>>

}
