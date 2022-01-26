package com.kiriuru.weatherappsev.currentWeather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.telecom.TelecomManager.EXTRA_LOCATION
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import coil.load
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.kiriuru.weatherappsev.App
import com.kiriuru.weatherappsev.BuildConfig
import com.kiriuru.weatherappsev.currentWeather.data.LocationService
import com.kiriuru.weatherappsev.currentWeather.data.model.WeatherResponse
import com.kiriuru.weatherappsev.databinding.FragmentWeatherBinding
import com.kiriuru.weatherappsev.utils.hasPermission
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


class WeatherFragment : Fragment() {
    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1001
    }

    private var foregroundLocationServiceBound = false
    private var locationService: LocationService? = null

//    private lateinit var foregroundBroadcastReceiver: ForegroundBroadcastReceiver
//
//    private val foregroundServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
//            val binder = service as LocationService.LocalBinder
//            locationService = binder.service
//            foregroundLocationServiceBound = true
//        }
//
//        override fun onServiceDisconnected(p0: ComponentName?) {
//            locationService = null
//            foregroundLocationServiceBound = false
//        }
//    }


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    private val viewModel by viewModels<WeatherFragmentViewModel> { viewModelFactory }


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var cancellationTokenSource = CancellationTokenSource()


    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = checkNotNull(_binding)

    private var latitude: Double = 0.0
    private var longityde: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
//        foregroundBroadcastReceiver = ForegroundBroadcastReceiver()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App)
            .appComponent.weatherComponent().create().inject(this)
    }

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


        if (permissionApproved()) {
            showToast("approved")

//            updateService(viewModel.receivingLocationUpdates.value)
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.receivingLocationUpdates.collect {
                        updateService(it)
                        showToast("update Service")
                    }
                }
            }
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.locationData.collect {
                        showToast("get loc data ${it.toString()}")
                        if (it != null) {
                            latitude = it.latitude
                            longityde = it.longitude
                            viewModel.setData("${it.latitude},${it.longitude}")
                        } else {
                            getLocation()
                        }
//                        delay(10000)
                    }
                }
            }
        } else {
            showToast("Not Approved")
            requestPermissions()
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

        binding.btn.setOnClickListener {
            viewModel.setData("Moscow")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(weatherData: WeatherResponse) {
        binding.currentTemp.text = weatherData.current.temp_c.toString()
        binding.currentData.text = weatherData.location.name + " " + weatherData.location.tz_id
        binding.currentWeatherText.text = weatherData.current.condition.text
        binding.feelsTempText.text = "$latitude,$longityde"

        setImage(weatherData.current.condition.icon)

    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        showToast("Alter get loc")
        val currentLocation: Task<Location> =
            fusedLocationProviderClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )

        currentLocation.addOnCompleteListener { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                val resultL: Location = task.result
                latitude = resultL.latitude
                longityde = resultL.longitude
                viewModel.setData("$latitude,$longityde")
            } else {
                showToast("Location Failure")
            }
        }
    }


    private fun showToast(textId: String) {
        Toast.makeText(context, textId, Toast.LENGTH_SHORT).show()
    }


    private fun setImage(icon: String) {
        binding.currentImageView.load("https:$icon")
    }


    private fun permissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestPermissions() {
        val provideRationale = permissionApproved()

        if (provideRationale) {
            Snackbar.make(
                binding.root,
                "Location permission needed for core functionality",
                Snackbar.LENGTH_LONG
            )
                .setAction("Ok") {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    showToast("User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> getLocation()
                else -> {
                    Snackbar.make(
                        binding.root,
                        "Permission was denied, but is needed for core functionality",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Settings") {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }


    private fun updateService(receive: Boolean) {
        if (receive) {
            showToast("stop service $receive")
            viewModel.stopLocationUpdates()
        } else {
            showToast("Start service $receive")
            viewModel.startLocationUpdates()

        }
    }



    override fun onPause() {
        if ((viewModel.receivingLocationUpdates.value==true) && !requireContext().hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            viewModel.stopLocationUpdates()
        }
        viewModel.stopLocationUpdates()

        super.onPause()
    }

    override fun onStop() {
        cancellationTokenSource.cancel()

        viewModel.stopLocationUpdates()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}