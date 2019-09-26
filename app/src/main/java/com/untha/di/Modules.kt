package com.untha.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.untha.model.database.ApplicationDatabase
import com.untha.model.dbservices.CategoryDbService
import com.untha.model.mappers.CategoryMapper
import com.untha.model.repositories.CategoryRepository
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.repositories.InformationCategoryRepository
import com.untha.model.repositories.SectionRepository
import com.untha.model.repositories.SectionStepRepository
import com.untha.model.services.LawsAndRightsServiceAPI
import com.untha.viewmodels.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
}

val persistenceModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            ApplicationDatabase::class.java, "application-database"
        ).build()
    }

    factory { CategoryRepository(get()) }
    factory { InformationCategoryRepository(get()) }
    factory { SectionRepository(get()) }
    factory { SectionStepRepository(get()) }
    factory { CategoryWithRelationsRepository(get()) }

    factory { CategoryDbService(get(), get(), get(), get()) }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            androidContext().packageName,
            Context.MODE_PRIVATE
        )
    }
}

val networkModule = module {
    single { LawsAndRightsServiceAPI().getLawsAndRightsService() }
}

val mapperModule = module {
    factory { CategoryMapper() }

}
