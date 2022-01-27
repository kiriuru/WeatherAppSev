package com.kiriuru.weatherappsev.currentWeather.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriuru.weatherappsev.currentWeather.data.model.LocationEntity
import com.kiriuru.weatherappsev.currentWeather.data.model.WeatherResponse
import com.kiriuru.weatherappsev.currentWeather.data.repository.LocationRepository
import com.kiriuru.weatherappsev.currentWeather.data.repository.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WeatherFragmentViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) :
    ViewModel() {

    companion object {
        const val TAG = "viewModel"
    }

    private val _data = MutableStateFlow<WeatherResponse?>(null)
    val data: StateFlow<WeatherResponse?> = _data.asStateFlow()

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    //Обновление данных о погоде
    fun setData(q: String) {
        viewModelScope.launch {
            _data.tryEmit(weatherRepository.getCurrentWeather(q = q))
        }
    }

    val locationData: StateFlow<LocationEntity?> = locationRepository.location

    val receivingLocationUpdates: StateFlow<Boolean> = locationRepository.receivingLocationUpdate


    //Вызовы сервиса
    fun startLocationUpdates() {
        viewModelScope.launch { locationRepository.startLocationUpdate() }
    }

    fun stopLocationUpdates() {
        viewModelScope.launch { locationRepository.stopLocationUpdate() }
    }

    //Запуск каунтара для автообновления
    fun update(isUpdate: Boolean) {
        viewModelScope.launch {
            while (true) {
                if (isUpdate) {
                    _count.tryEmit(value = _count.value + 1)
                    Log.d(TAG, "count = ${count.value}")
                    delay(TimeUnit.SECONDS.toMillis(1))
                    if (count.value == 30) {
                        _count.tryEmit(0)
                    } else if (!isUpdate) {
                        break
                    }
                }
            }
        }
    }


}