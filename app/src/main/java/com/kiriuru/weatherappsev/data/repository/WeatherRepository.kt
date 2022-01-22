package com.kiriuru.weatherappsev.data.repository

import com.kiriuru.weatherappsev.data.api.WeatherApi
import com.kiriuru.weatherappsev.model.WeatherResponse

class WeatherRepository(private val api: WeatherApi) {
    suspend fun getCurrentWeather(q: String) = api.getCurrentWeather(cityName = q)
}