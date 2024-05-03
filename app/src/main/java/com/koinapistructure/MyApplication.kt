package com.koinapistructure

import android.app.Application
import com.koinapistructure.di.ApiModule
import com.koinapistructure.di.RepoModule
import com.koinapistructure.di.ViewModelModule
import com.uxcam.UXCam
import com.uxcam.datamodel.UXConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


open class MyApplication :Application() {

    override fun onCreate() {
        super.onCreate()

        val config = UXConfig.Builder("fzymyov9m0vpt8w")
            .enableAutomaticScreenNameTagging(true)
            .enableImprovedScreenCapture(true)
            .enableMultiSessionRecord(true)
            .build()

        UXCam.startWithConfiguration(config);


        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyApplication)
            modules(ApiModule, ViewModelModule, RepoModule)
        }
    }
}