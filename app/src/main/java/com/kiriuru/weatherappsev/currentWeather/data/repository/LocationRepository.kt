package com.kiriuru.weatherappsev.currentWeather.data.repository

import android.content.Context
import android.location.Location
import androidx.annotation.MainThread
import com.kiriuru.weatherappsev.currentWeather.data.LocationService
import com.kiriuru.weatherappsev.currentWeather.data.model.LocationEntity
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ExecutorService
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

//    companion object{
//        @Volatile private var INSTANCE : LocationRepository? = null
//
//        fun getInstance(context: Context, executor: ExecutorService):LocationRepository{
//            return INSTANCE ?: synchronized(this){
//                INSTANCE ?: LocationRepository(LocationService.getInstance(context),executor).also { INSTANCE = it }
//            }
//        }
//    }
}