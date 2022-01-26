package com.kiriuru.weatherappsev.ui

import android.annotation.SuppressLint
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.kiriuru.weatherappsev.data.repository.WeatherRepository
import com.kiriuru.weatherappsev.model.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class WeatherFragmentViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

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

    fun update(q:String) {
        var count=0
        viewModelScope.launch {
            while (true) {
                count++
                delay(10000)
                _data.tryEmit(weatherRepository.getCurrentWeather(q))
            }
        }
    }



}