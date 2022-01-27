package com.kiriuru.weatherappsev.di

import com.kiriuru.weatherappsev.currentWeather.data.api.WeatherApi
import com.kiriuru.weatherappsev.utils.Const
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

@Module
class NetworkModule {

    @Provides
    fun provideNetworkService(): WeatherApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(Const.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }
}