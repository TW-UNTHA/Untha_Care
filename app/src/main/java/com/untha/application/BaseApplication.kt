package com.untha.application

import android.app.Application
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
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
