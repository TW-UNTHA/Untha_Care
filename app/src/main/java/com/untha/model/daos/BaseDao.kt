package com.untha.model.daos

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update
import com.untha.model.models.BaseModel

interface BaseDao<T : BaseModel> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    fun insert(vararg obj: T): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    fun insert(obj: List<T>): List<Long>

    @Delete
    fun delete(vararg obj: T)

    @Update
    fun update(vararg obj: T)
}
