package com.kiriuru.weatherappsev.di


import android.content.Context
import com.kiriuru.weatherappsev.currentWeather.data.LocationService
import dagger.Module
import dagger.Provides


@Module
class LocationModule {



    @Provides
    fun provideLocationService(context: Context): LocationService {
        return LocationService(context)
    }
}

