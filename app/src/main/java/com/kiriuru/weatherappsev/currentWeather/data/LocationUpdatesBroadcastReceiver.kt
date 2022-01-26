package com.kiriuru.weatherappsev.currentWeather.data

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationResult
import com.kiriuru.weatherappsev.currentWeather.data.model.LocationEntity
import com.kiriuru.weatherappsev.currentWeather.data.repository.LocationRepository
import com.kiriuru.weatherappsev.currentWeather.data.repository.LocationRepositoryImpl
import java.util.*
import java.util.concurrent.Executors

class LocationUpdatesBroadcastReceiver(private val service: LocationService): BroadcastReceiver() {
    companion object {
        const val TAG = "BroadcastReceiver"
        const val ACTION_PROCESS_UPDATES =
            "com.kiriuru.weatherappsev.currentWeather.data" +
                    "PROCESS_UPDATES"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if (intent.action == ACTION_PROCESS_UPDATES) {

                // Checks for location availability changes.
                LocationAvailability.extractLocationAvailability(intent).let { locationAvailability ->
                    if (!locationAvailability.isLocationAvailable) {
                        Log.d(TAG, "Location services are no longer available!")
                    }
                }

                LocationResult.extractResult(intent).let { locationResult ->
                    val locations = locationResult.locations.map { location ->
                        context?.let { isAppInForeground(it) }?.let {

                         val locEn =    LocationEntity(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                foreground = it,
                                )
                            service.getLocation(locEn)
                        }
                        Log.d(TAG, "$location")
                    }
//                    if (locations.isNotEmpty()){
//                        service.getLocation()
//                    }

                }
            }
        }
    }
//
    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        appProcesses.forEach { appProcess ->
            if (appProcess.importance ==
                ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == context.packageName) {
                return true
            }
        }
        return false
    }


}