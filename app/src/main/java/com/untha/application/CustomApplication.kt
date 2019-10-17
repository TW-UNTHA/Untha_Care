package com.untha.application

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.untha.R
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import javax.net.ssl.SSLContext
import kotlin.system.exitProcess


class CustomApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        installTls12()
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

    private fun installTls12() {
        try {
            ProviderInstaller.installIfNeeded(this)
            val sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (e: GooglePlayServicesRepairableException) {
            GoogleApiAvailability.getInstance()
                .showErrorNotification(this, e.connectionStatusCode)
        } catch (e: GooglePlayServicesNotAvailableException) {
            AlertDialog.Builder(this)
                .setTitle(applicationContext.getString(R.string.error))
                .setMessage(applicationContext.getString(R.string.error_description))
                .setNeutralButton(applicationContext.getString(R.string.salir))
                { _: DialogInterface, _: Int ->
                    exitProcess(0)
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }
}
