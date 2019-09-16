package com.untha_care.application

import android.app.Application
import com.untha_care.dependency_injection.networkModule
import com.untha_care.dependency_injection.persistenceModule
import com.untha_care.dependency_injection.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(listOf(persistenceModule, networkModule, viewModelsModule))
        }
    }
}