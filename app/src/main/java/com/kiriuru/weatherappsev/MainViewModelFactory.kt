package com.kiriuru.weatherappsev

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiriuru.weatherappsev.data.api.WeatherApi
import com.kiriuru.weatherappsev.data.repository.WeatherRepository
import com.kiriuru.weatherappsev.ui.WeatherFragmentViewModel

class MainViewModelFactory(private val api: WeatherApi) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WeatherFragmentViewModel::class.java)) {
            WeatherFragmentViewModel(weatherRepository = WeatherRepository(api)) as T
        } else {
            throw IllegalArgumentException("viewModel not found")
        }
    }
}