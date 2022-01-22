package com.kiriuru.weatherappsev.ui

import androidx.lifecycle.ViewModel
import com.kiriuru.weatherappsev.data.repository.WeatherRepository
import kotlinx.coroutines.flow.flow

class WeatherFragmentViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {


    fun getCurrentWeather(q: String) = flow {
        try {
            emit(weatherRepository.getCurrentWeather(q))
        } catch (exception: Exception) {

        }

    }


}