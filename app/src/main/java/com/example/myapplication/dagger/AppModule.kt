package com.example.myapplication.dagger

import com.example.myapplication.App
import com.example.myapplication.view.adapter.ForecastAdapter
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val app: App) {
    @Provides
    @Singleton
    fun provideApp() = app

    @Provides
    @Singleton
    fun provideAppContext(app: App) = app.applicationContext
}

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(forecastAdapterItem: ForecastAdapter.ForecastAdapterItem)
}