package com.kiriuru.weatherappsev.currentWeather.data.repository

import com.kiriuru.weatherappsev.currentWeather.data.LocationService
import com.kiriuru.weatherappsev.currentWeather.data.model.LocationEntity
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


interface LocationRepository {
    suspend fun startLocationUpdate()
    suspend fun stopLocationUpdate()
    val receivingLocationUpdate: StateFlow<Boolean>
    val location: StateFlow<LocationEntity?>
}

class LocationRepositoryImpl @Inject constructor(
    private val locationService: LocationService
) : LocationRepository {

    override val receivingLocationUpdate: StateFlow<Boolean> =
        locationService.receivingLocationUpdates
    override val location: StateFlow<LocationEntity?> = locationService.locationData


    override suspend fun startLocationUpdate() = locationService.startLocationUpdate()


    override suspend fun stopLocationUpdate() = locationService.stopLocationUpdates()

}