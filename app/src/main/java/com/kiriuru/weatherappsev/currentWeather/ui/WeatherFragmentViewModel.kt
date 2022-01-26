package com.kiriuru.weatherappsev.currentWeather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriuru.weatherappsev.currentWeather.data.repository.WeatherRepository
import com.kiriuru.weatherappsev.currentWeather.model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherFragmentViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {

    private val _isPermissionGranted = MutableStateFlow<Boolean?>(false)
    val isPermissionGranted: StateFlow<Boolean?> = _isPermissionGranted.asStateFlow()

    private val _data = MutableStateFlow<WeatherResponse?>(null)
    val data: StateFlow<WeatherResponse?> = _data.asStateFlow()


    fun setPermissionGranted(value: Boolean) {
        _isPermissionGranted.value = value
    }

    fun setData(q: String) {
        viewModelScope.launch {
            _data.tryEmit(weatherRepository.getCurrentWeather(q = q))
        }
    }
//
//    fun update(q: String) {
//        var count = 0
//        viewModelScope.launch {
//            while (true) {
//                count++
//                delay(10000)
//                _data.tryEmit(weatherRepository.getCurrentWeather(q))
//            }
//        }
//    }

//    fun getCurrentWeather(q: String) = flow {
//        try {
//            emit(weatherRepository.getCurrentWeather(q))
//        } catch (exception: Exception) {
//
//        }
//
//    }


}