package com.kiriuru.weatherappsev.currentWeather.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.kiriuru.weatherappsev.currentWeather.data.model.LocationEntity
import com.kiriuru.weatherappsev.currentWeather.data.model.WeatherResponse
import com.kiriuru.weatherappsev.utils.hasPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


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
//
//    private var configurationChange = false
//    private var serviceRunningForeground = false
//    private val locationBinder = LocalBinder()
//
//
//    private lateinit var locationCallback: LocationCallback
//    private var currentLocation: Location? = null

    fun getLocation(locationData:LocationEntity) {
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

//    companion object {
//        @Volatile
//        private var INSTANCE: LocationService? = null
//        fun getInstance(context: Context): LocationService {
//            return INSTANCE ?: synchronized(this) {
//                INSTANCE ?: LocationService(context).also { INSTANCE = it }
//            }
//        }
//    }


//    override fun onCreate() {
//
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//                currentLocation = locationResult.lastLocation
//                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
//                intent.putExtra(EXTRA_LOCATION, currentLocation)
//                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
//            }
//        }
//    }
//
//
//    override fun onBind(p0: Intent?): IBinder {
//        stopForeground(true)
//        serviceRunningForeground = false
//        configurationChange = false
//        return locationBinder
//    }
//
//    override fun onRebind(intent: Intent?) {
//        stopForeground(true)
//        serviceRunningForeground = false
//        configurationChange = false
//        super.onRebind(intent)
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        configurationChange = true
//    }
//
//    fun subscribeLocationUpdate() {
//        startService(Intent(applicationContext, LocationService::class.java))
//        try {
//            fusedLocationProviderClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback,
//                Looper.getMainLooper()
//            )
//        } catch (e: SecurityException) {
//
//        }
//    }
//
//    @SuppressLint("LongLogTag")
//    fun unSubscribeLocationUpdate() {
//        try {
//            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
//            removeTask.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "Location Callback removed.")
//                    stopSelf()
//                } else {
//                    Log.d(TAG, "Failed to remove Location Callback.")
//                }
//            }
//        } catch (e: SecurityException) {
//            Log.d(TAG, "Failed to $e")
//        }
//    }
//
//    inner class LocalBinder : Binder() {
//        internal val service: LocationService
//            get() = this@LocationService
//    }
//
//    companion object {
//        private const val TAG = "Foreground_Location_Service"
//        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
//            " com.kiriuru.weatherappsev.action.FOREGROUND_ONLY_LOCATION_BROADCAST"
//        internal const val EXTRA_LOCATION = " com.kiriuru.weatherappsev.extra.LOCATION"
//    }
}