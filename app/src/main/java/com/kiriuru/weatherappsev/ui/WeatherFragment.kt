package com.kiriuru.weatherappsev.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.kiriuru.weatherappsev.MainViewModelFactory
import com.kiriuru.weatherappsev.data.api.RetrofitBuilder
import com.kiriuru.weatherappsev.databinding.FragmentWeatherBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class WeatherFragment : Fragment() {
    private val viewModel: WeatherFragmentViewModel by viewModels {
        MainViewModelFactory(api = RetrofitBuilder.apiService)
    }
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = checkNotNull(_binding)

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
        binding.btn.setOnClickListener { getWeather() }
    }

    private fun getWeather() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getCurrentWeather("Москва").collect {
                    binding.currentTemp.text = it.current.temp_c.toString()
                    binding.currentData.text = it.location.name + " " + it.location.tz_id
                    binding.currentWeatherText.text = it.current.condition.text

                    setImage(it.current.condition.icon)
                }
            }
        }
    }

    private fun setImage(icon: String) {
        binding.currentImageView.load("https:$icon")

    }

}