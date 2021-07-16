package com.geniecustomer.base

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.geniecustomer.di.retrofitModule
import com.geniecustomer.di.viewmodelModule
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {

    companion object {
        lateinit var gson: Gson
    }
    override fun onCreate() {
        super.onCreate()
        gson = Gson()

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(retrofitModule, viewmodelModule))
        }
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

    }
}