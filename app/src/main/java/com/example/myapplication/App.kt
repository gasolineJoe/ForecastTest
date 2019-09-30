package com.example.myapplication

import android.app.Application
import com.example.myapplication.dagger.DaggerNetworkComponent
import com.example.myapplication.dagger.NetworkComponent
import com.example.myapplication.dagger.NetworkModule

class App : Application() {

    companion object {
        lateinit var component: NetworkComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerNetworkComponent
            .builder()
            .build()
    }
}