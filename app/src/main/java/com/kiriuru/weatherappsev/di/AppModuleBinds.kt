package com.kiriuru.weatherappsev.di

import com.kiriuru.weatherappsev.currentWeather.data.repository.LocationRepository
import com.kiriuru.weatherappsev.currentWeather.data.repository.LocationRepositoryImpl
import com.kiriuru.weatherappsev.currentWeather.data.repository.WeatherRepository
import com.kiriuru.weatherappsev.currentWeather.data.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface AppModuleBinds {

    @Suppress("WeatherRepo")
    @Binds
    fun bindsRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository

    @Suppress("LocationRepo")
    @Binds
    fun bindsLocationRepository(locationRepository: LocationRepositoryImpl): LocationRepository
}