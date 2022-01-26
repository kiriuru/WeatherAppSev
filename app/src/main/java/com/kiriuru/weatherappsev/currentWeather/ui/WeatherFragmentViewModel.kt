package com.kiriuru.weatherappsev.currentWeather.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriuru.weatherappsev.currentWeather.data.model.LocationEntity
import com.kiriuru.weatherappsev.currentWeather.data.model.WeatherResponse
import com.kiriuru.weatherappsev.currentWeather.data.repository.LocationRepository
import com.kiriuru.weatherappsev.currentWeather.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherFragmentViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) :
    ViewModel() {

    private val _data = MutableStateFlow<WeatherResponse?>(null)
    val data: StateFlow<WeatherResponse?> = _data.asStateFlow()

    //    private val _mLocationData = MutableStateFlow<Location?>(null)
//        var mLocationData: StateFlow<Location?> = _mLocationData.asStateFlow()
    fun setData(q: String) {
        viewModelScope.launch {
            _data.tryEmit(weatherRepository.getCurrentWeather(q = q))
        }
    }

    val locationData: StateFlow<LocationEntity?> = locationRepository.location

    val receivingLocationUpdates: StateFlow<Boolean> = locationRepository.receivingLocationUpdate

    fun startLocationUpdates() {
        viewModelScope.launch { locationRepository.startLocationUpdate() }
    }

    fun stopLocationUpdates() {
        viewModelScope.launch { locationRepository.stopLocationUpdate() }
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


}