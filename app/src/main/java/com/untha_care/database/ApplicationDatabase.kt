package com.untha_care.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.untha_care.model.daos.BaseDao
import com.untha_care.model.daos.CategoryDao
import com.untha_care.model.models.BaseModel
import com.untha_care.model.models.Category

@Database(entities = [Category::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {

    //Example
    abstract fun categoryDao(): CategoryDao
//    abstract fun <T: BaseModel> getBDao(): BaseDao<T>
}