package com.kiriuru.weatherappsev.currentWeather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kiriuru.weatherappsev.App
import com.kiriuru.weatherappsev.currentWeather.model.WeatherResponse
import com.kiriuru.weatherappsev.databinding.FragmentWeatherBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


class WeatherFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    private val viewModel by viewModels<WeatherFragmentViewModel> { viewModelFactory }


    private lateinit var fused: FusedLocationProviderClient


    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = checkNotNull(_binding)

    private var latitude: Double = 0.0
    private var longityde: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fused = LocationServices.getFusedLocationProviderClient(requireContext())
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
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(weatherData: WeatherResponse) {
        binding.currentTemp.text = weatherData.current.temp_c.toString()
        binding.currentData.text = weatherData.location.name + " " + weatherData.location.tz_id
        binding.currentWeatherText.text = weatherData.current.condition.text
        binding.feelsTempText.text = "$latitude,$longityde"

        setImage(weatherData.current.condition.icon)
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