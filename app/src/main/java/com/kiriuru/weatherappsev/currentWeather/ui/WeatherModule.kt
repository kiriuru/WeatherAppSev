package com.kiriuru.weatherappsev.currentWeather.ui

import androidx.lifecycle.ViewModel
import com.kiriuru.weatherappsev.di.MainViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap


@Subcomponent(modules = [WeatherModule::class])
interface WeatherComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): WeatherComponent
    }

    fun inject(fragment: WeatherFragment)
}


@Module
abstract class WeatherModule {
    @Binds
    @IntoMap
    @MainViewModelKey(WeatherFragmentViewModel::class)
    abstract fun bindViewModel(viewModel: WeatherFragmentViewModel): ViewModel
}