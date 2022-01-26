package com.kiriuru.weatherappsev.currentWeather.data.api

import com.kiriuru.weatherappsev.currentWeather.model.WeatherResponse
import com.kiriuru.weatherappsev.utils.Const
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {
    @GET("current.json?")
    suspend fun getCurrentWeather(
        @Query("key") key: String = Const.API_KEY,
        @Query("q") cityName: String,
        @Query("lang") lang: String = "ru"
    ): WeatherResponse
}