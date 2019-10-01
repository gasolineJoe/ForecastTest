package com.example.myapplication

import android.app.Application
import com.example.myapplication.dagger.*

class App : Application() {

    companion object {
        lateinit var network: NetworkComponent
        lateinit var app: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        network = DaggerNetworkComponent
            .builder()
            .build()
        app = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}