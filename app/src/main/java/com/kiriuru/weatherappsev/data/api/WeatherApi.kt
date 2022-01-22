package com.kiriuru.weatherappsev.data.api

import com.kiriuru.weatherappsev.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {
    @GET("current.json?")
    suspend fun getCurrentWeather(
        @Query("key") key: String = "684839d990b14128823115257222201",
        @Query("q") cityName: String,
        @Query("lang") lang: String = "ru"
    ): WeatherResponse
}