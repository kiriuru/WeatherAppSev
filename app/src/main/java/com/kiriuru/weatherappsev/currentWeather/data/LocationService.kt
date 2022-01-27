package com.kiriuru.weatherappsev.currentWeather.data

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.kiriuru.weatherappsev.currentWeather.data.model.LocationEntity
import com.kiriuru.weatherappsev.utils.hasPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit


//Менеджер геолокаци, взятый из гугл примеров, не много переписанный,
//в целом работает, кроме получения данных с броадкаста
class LocationService(private val context: Context) {

    private val _receivingLocationUpdates: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val receivingLocationUpdates: StateFlow<Boolean>
        get() = _receivingLocationUpdates

    private val _locationData = MutableStateFlow<LocationEntity?>(null)
    var locationData: StateFlow<LocationEntity?> = _locationData.asStateFlow()


    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = TimeUnit.SECONDS.toMillis(60)
        fastestInterval = TimeUnit.SECONDS.toMillis(30)
        maxWaitTime = TimeUnit.MINUTES.toMillis(2)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun getLocation(locationData: LocationEntity) {
        _locationData.tryEmit(locationData)

    }

    private val locationUpdatePendingIntent: PendingIntent by lazy {
        val intent =
            Intent(context, LocationUpdatesBroadcastReceiver::class.java)
        intent.action = LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    fun startLocationUpdate() {
        if (!context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return
        try {
            _receivingLocationUpdates.value = true

            fusedLocationClient.requestLocationUpdates(locationRequest, locationUpdatePendingIntent)
        } catch (permissionRevoked: SecurityException) {
            _receivingLocationUpdates.value = false

//            Log.d(TAG, "Location permission revoked; details: $permissionRevoked")
            throw permissionRevoked
        }

    }


    fun stopLocationUpdates() {
        //    Log.d(TAG, "stopLocationUpdates()")
        _receivingLocationUpdates.value = false
        fusedLocationClient.removeLocationUpdates(locationUpdatePendingIntent)
    }
}