package com.kiriuru.weatherappsev.di

import com.kiriuru.weatherappsev.currentWeather.data.repository.WeatherRepository
import com.kiriuru.weatherappsev.currentWeather.data.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface AppModuleBinds {

    @Suppress("somebody name")
    @Binds
    fun bindsRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository
}