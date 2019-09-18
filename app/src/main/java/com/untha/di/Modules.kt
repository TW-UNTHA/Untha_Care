package com.untha.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.untha.database.ApplicationDatabase
import com.untha.model.repositories.CategoryRepository
import com.untha.model.services.LawsAndRightsServiceAPI
import com.untha.viewmodels.ExampleViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { ExampleViewModel(get(), get()) }
}

val persistenceModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            ApplicationDatabase::class.java, "application-database"
        ).build()
    }

    factory { CategoryRepository(get()) }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "SharedPreferences",
            Context.MODE_PRIVATE
        )
    }

}

val networkModule = module {
    single { LawsAndRightsServiceAPI().getLawsAndRightsService() }

}
