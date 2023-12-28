package com.koinapistructure

import android.app.Application
import com.koinapistructure.di.ApiModule
import com.koinapistructure.di.RepoModule
import com.koinapistructure.di.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

open class MyApplication :Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyApplication)
            modules(ApiModule, ViewModelModule, RepoModule)
        }
    }
}