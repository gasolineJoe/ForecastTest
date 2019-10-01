package com.example.myapplication

import android.app.Application
import com.example.myapplication.dagger.*
import net.danlew.android.joda.JodaTimeAndroid

class App : Application() {

    companion object {
        lateinit var network: NetworkComponent
        lateinit var app: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
        network = DaggerNetworkComponent
            .builder()
            .build()
        app = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}