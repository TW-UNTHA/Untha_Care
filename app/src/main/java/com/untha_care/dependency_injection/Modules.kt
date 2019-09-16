package com.untha_care.dependency_injection

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.untha_care.database.ApplicationDatabase
import com.untha_care.model.services.LawsAndRightsServiceAPI
import com.untha_care.viewmodels.ExampleViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module(override = true) {
    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "SharedPreferences",
            Context.MODE_PRIVATE
        )
    }
    single {
        Room.databaseBuilder(
            androidContext(),
            ApplicationDatabase::class.java, "application-database"
        ).build()
    }
    single { LawsAndRightsServiceAPI().getLawsAndRightsService() }
    viewModel { ExampleViewModel(get(), get()) }
}