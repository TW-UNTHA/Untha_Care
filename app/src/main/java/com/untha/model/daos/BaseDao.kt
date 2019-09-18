package com.untha.model.daos

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.untha.model.models.BaseModel

interface BaseDao<T: BaseModel> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: T)

    @Delete ()
    fun delete(vararg obj: T)

    @Update
    fun update(vararg obj: T)
}
