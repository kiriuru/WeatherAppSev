package com.kiriuru.weatherappsev.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.kiriuru.weatherappsev.MainViewModelFactory
import com.kiriuru.weatherappsev.data.api.RetrofitBuilder
import com.kiriuru.weatherappsev.databinding.FragmentWeatherBinding
import com.kiriuru.weatherappsev.model.WeatherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@ExperimentalCoroutinesApi
class WeatherFragment : Fragment(), LocationListener {

    private val viewModel: WeatherFragmentViewModel by viewModels {
        MainViewModelFactory(api = RetrofitBuilder.apiService)
    }
    private lateinit var fused: FusedLocationProviderClient


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fused = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val locationPermissionReq =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                    when {
                        permission.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            true
                        ) -> {
                            viewModel.setPermissionGranted(true)
                            showToast("Precise location access granted.")
                        }
                        permission.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            true
                        ) -> {
                            viewModel.setPermissionGranted(true)
                            showToast("Only approximate location access granted.")
                        }
                        else -> {
                            viewModel.setPermissionGranted(false)
                        }
                    }
                }
            locationPermissionReq.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        showToast(viewModel.isPermissionGranted.value.toString() + " 2")
        if (viewModel.isPermissionGranted.value == false) {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    fused.lastLocation.addOnSuccessListener {
                        latitude = it.latitude
                        longityde = it.longitude
                        showToast("get location ")
                        viewModel.setData("$latitude,$longityde")
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collect {
                    if (it != null) {
                        updateUI(it)
                    }
                }
            }
        }
        binding.btn.setOnClickListener { viewModel.setData("Moscow") }
        binding.buttonUpd.setOnClickListener {
            fused.lastLocation.addOnSuccessListener {
                latitude = it.latitude
                longityde = it.longitude
            }

            viewModel.update("$latitude,$longityde")
        }
    }

    private fun updateUI(weatherData: WeatherResponse) {
        binding.currentTemp.text = weatherData.current.temp_c.toString()
        binding.currentData.text = weatherData.location.name + " " + weatherData.location.tz_id
        binding.currentWeatherText.text = weatherData.current.condition.text
        binding.feelsTempText.text = "$latitude,$longityde"

        setImage(weatherData.current.condition.icon)
    }

    override fun onLocationChanged(p0: Location) {
        viewModel.setData("${p0.latitude},${p0.longitude}")
    }


    private fun showToast(textId: String) {
        Toast.makeText(context, textId, Toast.LENGTH_SHORT).show()
    }

    private fun setImage(icon: String) {
        binding.currentImageView.load("https:$icon")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

}

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        showToast("Request  $requestCode")
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            showToast("$requestCode  if")
//            var allPermissionsGranted = true
//            if (grantResults.isNotEmpty()) {
//                grantResults.forEach { permissionResult ->
//                    if (permissionResult != PackageManager.PERMISSION_GRANTED) {
//                        allPermissionsGranted = false
//                        viewModel.setPermissionGranted(false)
//                        showToast("get permission " + viewModel.isPermissionGranted.value.toString() + " 3")
//                    }
//                }
//                if (allPermissionsGranted) {
//                    viewModel.setPermissionGranted(true)
//                    showToast("get permission " + viewModel.isPermissionGranted.value.toString() + " 3")
//                    viewModel.setPermissionGranted(true)
//                }
//            }
//
//        }
//    }
//


//    @SuppressLint("MissingPermission")
//    fun getLocation(): Flow<MapLoc> = callbackFlow {
//        val locationReq = LocationRequest.create().apply {
//            interval = TimeUnit.SECONDS.toMillis(60)
//            fastestInterval = TimeUnit.SECONDS.toMillis(30)
//            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
//            priority = PRIORITY_HIGH_ACCURACY
//        }
//        val callback = object : LocationCallback() {
//            override fun onLocationResult(result: LocationResult) {
//                super.onLocationResult(result)
//                val locationResult = result.lastLocation
//                userLocation =
//                    MapLoc(lat = locationResult.latitude, lon = locationResult.longitude)
//                latitude = locationResult.latitude
//
//                longityde = locationResult.longitude
//                showToast("$latitude, $longityde")
//                try {
//                    this@callbackFlow.trySend(userLocation).isSuccess
//                } catch (e: Exception) {
//                }
//            }
//        }
//
//        fused.requestLocationUpdates(locationReq, callback, Looper.getMainLooper())
//            .addOnFailureListener { e -> close(e) }
//            .addOnCompleteListener { fused.removeLocationUpdates(callback) }
//        awaitClose { fused.removeLocationUpdates(callback) }
//    }
