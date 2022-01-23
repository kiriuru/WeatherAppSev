package com.kiriuru.weatherappsev.ui

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.kiriuru.weatherappsev.MainViewModelFactory
import com.kiriuru.weatherappsev.data.api.RetrofitBuilder
import com.kiriuru.weatherappsev.databinding.FragmentWeatherBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class WeatherFragment : Fragment() {
    private val viewModel: WeatherFragmentViewModel by viewModels {
        MainViewModelFactory(api = RetrofitBuilder.apiService)
    }
    private lateinit var fused: FusedLocationProviderClient
    private lateinit var locationReq: com.google.android.gms.location.LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var currentLocation: Location? = null

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = checkNotNull(_binding)

    private var latitude: Double = 0.0
    private var longityde: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fused = LocationServices.getFusedLocationProviderClient(view.context)

        locationReq = com.google.android.gms.location.LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        if (ContextCompat.checkSelfPermission(
                view.context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                view.context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity as Activity,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1
            )

        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation.let {
                    currentLocation = it
                    latitude = currentLocation!!.latitude
                    longityde = currentLocation!!.longitude
                }
            }
        }
        Looper.myLooper()
            ?.let {
                fused.requestLocationUpdates(locationReq, locationCallback, it)
            }

        binding.btn.setOnClickListener { getWeather() }
    }

    private fun getWeather() {

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getCurrentWeather("$latitude,$longityde").collect {
                    binding.currentTemp.text = it.current.temp_c.toString()
                    binding.currentData.text = it.location.name + " " + it.location.tz_id
                    binding.currentWeatherText.text = it.current.condition.text
                    binding.feelsTempText.text = "$latitude,$longityde"

                    setImage(it.current.condition.icon)
                }
            }
        }
    }


    private fun setImage(icon: String) {
        binding.currentImageView.load("https:$icon")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        fused.removeLocationUpdates(locationCallback)
    }

}