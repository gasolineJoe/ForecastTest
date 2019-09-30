package com.example.myapplication.network

import com.example.myapplication.network.pojo.Forecast
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


const val OPENWEATHER_API_ADDRESS = "https://api.openweathermap.org"
const val OPENWEATHER_API_KEY = "e46350fbb19b696e1942cbeabf862d09"

interface IForecast {
    @GET("/data/2.5//forecast")
    fun getForecast(@Query("q") cityName: String): Observable<Forecast>
}