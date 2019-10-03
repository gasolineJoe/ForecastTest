package com.example.myapplication.dagger

import com.example.myapplication.view.activity.MainActivity
import com.example.myapplication.network.OPENWEATHER_API_ADDRESS
import com.example.myapplication.network.OPENWEATHER_API_KEY
import com.example.myapplication.network.IForecast
import com.example.myapplication.view.activity.ForecastPresenter
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttp() = OkHttpClient.Builder().addInterceptor { chain ->
        val url = chain
            .request().url()
            .newBuilder()
            .addQueryParameter("appid", OPENWEATHER_API_KEY)
            .addQueryParameter("units", "metric")
            .build()
        val newRequest = chain.request().newBuilder().url(url).build()
        chain.proceed(newRequest)
    }.build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .baseUrl(OPENWEATHER_API_ADDRESS)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Provides
    fun provideForecastImpl(retrofit: Retrofit): IForecast =
        retrofit.create(IForecast::class.java)
}

@Singleton
@Component(modules = [NetworkModule::class])
interface NetworkComponent {
    fun inject(activity: MainActivity)
    fun inject(presenter: ForecastPresenter)
}