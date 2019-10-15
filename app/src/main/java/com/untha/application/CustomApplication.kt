package com.untha.application

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.android.gms.security.ProviderInstaller
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import javax.net.ssl.SSLContext

class CustomApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        ProviderInstaller.installIfNeeded(applicationContext)
        val sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(null, null, null)
        sslContext.createSSLEngine()

        startKoin {
            androidLogger()
            androidContext(this@CustomApplication)
            modules(listOf(persistenceModule, networkModule, viewModelsModule, mapperModule))
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
