package com.untha.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.untha.model.converters.DateConverter
import com.untha.model.daos.CategoryDao
import com.untha.model.daos.CategoryInformationDao
import com.untha.model.daos.CategoryWithRelationsDao
import com.untha.model.daos.SectionModelDao
import com.untha.model.daos.SectionStepDao
import com.untha.model.models.CategoryInformationModel
import com.untha.model.models.CategoryModel
import com.untha.model.models.SectionModel
import com.untha.model.models.SectionStepModel

@Database(
    entities = [CategoryModel::class, CategoryInformationModel::class,
        SectionModel::class, SectionStepModel::class],
    version = 4,exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class ApplicationDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun sectionDao(): SectionModelDao
    abstract fun sectionStepDao(): SectionStepDao
    abstract fun categoryInformationDao(): CategoryInformationDao
    abstract fun categoryWithRelationsDao(): CategoryWithRelationsDao

}
