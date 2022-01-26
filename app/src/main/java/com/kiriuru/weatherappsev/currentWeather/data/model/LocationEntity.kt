package com.kiriuru.weatherappsev.currentWeather.data.model

data class LocationEntity(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val foreground: Boolean = true
) {
}