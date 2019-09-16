package com.untha_care.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.untha_care.model.daos.BaseDao
import com.untha_care.model.models.BaseModel

@Database(entities = [BaseModel::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {

    //Example
//    abstract fun <T: BaseModel> getADao(): BaseDao<T>
//    abstract fun <T: BaseModel> getBDao(): BaseDao<T>
}