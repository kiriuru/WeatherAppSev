package com.kiriuru.weatherappsev.di

import android.content.Context
import com.kiriuru.weatherappsev.currentWeather.data.repository.WeatherRepository
import com.kiriuru.weatherappsev.currentWeather.ui.WeatherComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module

@Component(
    modules = [
        AppModuleBinds::class,
        ViewModelBuilderModule::class,
        NetworkModule::class,
        SubcomponentModule::class
    ]
)

interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    fun weatherComponent(): WeatherComponent.Factory

    val weatherRepository: WeatherRepository
}

@Module(subcomponents = [WeatherComponent::class])
object SubcomponentModule