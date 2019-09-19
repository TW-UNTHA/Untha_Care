package com.untha.database

import android.app.Application
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.untha.model.models.Category
import com.untha.model.daos.RightsDao


@Database(entities = [Category::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {

    //Example
    abstract fun categoryDao(): RightsDao
//    abstract fun <T: BaseModel> getBDao(): BaseDao<T>

    companion object {
        private val lock = Any()
        private const val DB_NAME = "Category3.db"
        private var INSTANCE: ApplicationDatabase? = null

        fun getInstance(application: Application): ApplicationDatabase {
            synchronized(ApplicationDatabase.lock) {
                if (ApplicationDatabase.INSTANCE == null) {
                    ApplicationDatabase.INSTANCE =
                        Room.databaseBuilder(
                            application,
                            ApplicationDatabase::class.java,
                            ApplicationDatabase.DB_NAME
                        )
                            .allowMainThreadQueries()
                            .addCallback(object : RoomDatabase.Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    ApplicationDatabase.INSTANCE?.let {
                                        ApplicationDatabase.prePopulate(
                                            it,
                                            CategoryInfoProvider.categoryList
                                        )
                                    }
                                }
                            })
                            .build()
                }
                return ApplicationDatabase.INSTANCE!!
            }
        }

        fun prePopulate(database: ApplicationDatabase, categoryList: List<Category>) {
            for (category in categoryList) {
                AsyncTask.execute { database.categoryDao().insert(category) }
            }
        }
    }
}