package com.kiriuru.weatherappsev.currentWeather.data

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class ForegroundLocationService : Service() {
    private var configurationChange = false
    private var serviceRunningForeground = false
    private val locationBinder = LocalBinder()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    override fun onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult.lastLocation
                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }



    override fun onBind(p0: Intent?): IBinder {
        stopForeground(true)
        serviceRunningForeground = false
        configurationChange = false
        return locationBinder
    }

    override fun onRebind(intent: Intent?) {
        stopForeground(true)
        serviceRunningForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    fun subscribeLocationUpdate() {
        startService(Intent(applicationContext, ForegroundLocationService::class.java))
        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {

        }
    }

    @SuppressLint("LongLogTag")
    fun unSubscribeLocationUpdate() {
        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Location Callback removed.")
                    stopSelf()
                } else {
                    Log.d(TAG, "Failed to remove Location Callback.")
                }
            }
        } catch (e: SecurityException) {
            Log.d(TAG, "Failed to $e")
        }
    }

    inner class LocalBinder : Binder() {
        internal val service: ForegroundLocationService
            get() = this@ForegroundLocationService
    }

    companion object {
        private const val TAG = "Foreground_Location_Service"
        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
            " com.kiriuru.weatherappsev.action.FOREGROUND_ONLY_LOCATION_BROADCAST"
        internal const val EXTRA_LOCATION = " com.kiriuru.weatherappsev.extra.LOCATION"
    }
}