package com.untha_care.model.daos

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.untha_care.model.models.BaseModel

interface BaseDao<T: BaseModel> {
    @Insert
    fun insert(vararg obj: T)

    @Delete
    fun delete(vararg obj: T)

    @Update
    fun update(vararg obj: T)
}