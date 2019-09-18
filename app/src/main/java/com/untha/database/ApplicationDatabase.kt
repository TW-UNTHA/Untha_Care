package com.untha.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.untha.model.daos.CategoryDao
import com.untha.model.models.Category

@Database(entities = [Category::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {

    //Example
    abstract fun categoryDao(): CategoryDao
}
