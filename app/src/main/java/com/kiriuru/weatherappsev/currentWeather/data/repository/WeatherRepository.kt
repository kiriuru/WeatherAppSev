package com.kiriuru.weatherappsev.currentWeather.data.repository

import com.kiriuru.weatherappsev.currentWeather.data.api.WeatherApi
import com.kiriuru.weatherappsev.currentWeather.data.model.WeatherResponse
import javax.inject.Inject

interface WeatherRepository {
    suspend fun getCurrentWeather(q: String): WeatherResponse
}

class WeatherRepositoryImpl @Inject constructor(private val api: WeatherApi) : WeatherRepository {

    override suspend fun getCurrentWeather(q: String) = api.getCurrentWeather(cityName = q)
}