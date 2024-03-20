package com.example.weatherforecastapplication.favorite.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymvvmapplication.favproduct.viewmodel.FAVWeatherViewModel
import com.example.mymvvmapplication.favproduct.viewmodel.FAVWeatherViewModelFactory
import com.example.weatherforecastapplication.MainActivity
import com.example.weatherforecastapplication.MapActivity
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.databinding.FragmentFavoriteBinding
import com.example.weatherforecastapplication.db.WeatherLocalDataSource
import com.example.weatherforecastapplication.home.HomeViewModel
import com.example.weatherforecastapplication.home.HomeViewModelFactory
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherRepositoryImp
import com.example.weatherforecastapplication.network.ApiState
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.Locale

@Suppress("UNREACHABLE_CODE")
class FavoriteFragment : Fragment(),FavOnClickListener {
lateinit var btnAdd: FloatingActionButton
    private var _binding: FragmentFavoriteBinding? = null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var language: String
    lateinit var speed: String
    lateinit var temp: String
    private lateinit var hviewModel: HomeViewModel
    private lateinit var favFactory: FAVWeatherViewModelFactory
    private lateinit var viewModel: FAVWeatherViewModel
    private lateinit var favRecyclerView: RecyclerView
    private lateinit var favAdapter: FAVWeatherAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        sharedPreferences =
            requireActivity().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            favFactory = FAVWeatherViewModelFactory(
                WeatherRepositoryImp.getInstance(
                    RemoteDataSourceImpl(),
                    WeatherLocalDataSource(requireContext())
                )
            )

            viewModel = ViewModelProvider(this, favFactory).get(FAVWeatherViewModel::class.java)
        checkStoredData(sharedPreferences)

            val repository = WeatherRepositoryImp.getInstance(RemoteDataSourceImpl(),WeatherLocalDataSource(requireContext()))
        val viewModelFactory = HomeViewModelFactory(repository)
        hviewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        val units = getUnits()
        changeLanguage(language)
        lifecycleScope.launch {
           hviewModel.currentWeather.collect { result ->
                when (result) {
                    is ApiState.Success -> {
                       viewModel.insertWeather(result.data)
                    }

                    else -> {
                        Log.i("TAG", "onViewCreated: Error")
                    }
                }
            }
        }
        val selectedLatLng = arguments?.getParcelable<LatLng>("selectedLatLng")
        selectedLatLng?.let { latLng ->
                    if (units == "null")
                        hviewModel.getCurrentWeather(
                            latLng.latitude,
                            latLng.longitude,
                            language,
                            "39b555dfd3fff6499b8c963689da8e9c"
                        )
                    else
                       hviewModel.getCurrentWeather(
                           latLng.latitude,
                           latLng.longitude,
                            language,
                            units,
                            "39b555dfd3fff6499b8c963689da8e9c"
                        )
                }

        favRecyclerView = view.findViewById(R.id.list_item)
        favAdapter = FAVWeatherAdapter(this,this)
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        favRecyclerView.layoutManager = layoutManager
        favRecyclerView.adapter = favAdapter


        viewModel.weathres.observe(viewLifecycleOwner) {
            favAdapter.submitList(it)
            Log.i("TAG", "onViewCreated: list Size =  ${it.size}")
        }

         btnAdd=view.findViewById(R.id.btnAddLoction)
         btnAdd.setOnClickListener{
            val intent= Intent(context, MapActivity::class.java)
                .putExtra("id","fav")
            startActivity(intent)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun changeLanguage(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resources = resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
    private fun getUnits(): String {
        return when {
            speed == "meter/sec" && temp == "Fahrenheit" -> "metric"
            speed == "miles/hour" && temp == "Celsius" -> "imperial"
            speed == "miles/hour" && temp == "Fahrenheit" -> "imperial"
            speed == "meter/sec" && temp == "Celsius" -> "metric"
            else -> "null"
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkStoredData(sharedPreferences: SharedPreferences) {
        val languageId = sharedPreferences.getInt("Languages", R.id.rBtnLanguages)
        language = when (languageId) {
            R.id.rBtnLanguages -> "en"
            else -> "ar"
        }

        val speedId = sharedPreferences.getInt("Speed", R.id.rBtnSpeed)
        speed = when (speedId) {
            R.id.rBtnSpeed -> "meter/sec"
            R.id.rBtn2Speed -> "miles/hour"
            else -> "meter/sec"


        }
        val tempId = sharedPreferences.getInt("Temperature", R.id.rBtnTemperature)
        temp = when (tempId) {
            R.id.rBtn2Temperature -> "Celsius"
            R.id.rBtn3Temperature -> "Fahrenheit"
            else -> "Kelvin"
        }
    }

    override fun deleteItem(weatherData: WeatherData) {
        viewModel.deleteProduct(weatherData)
    }

    override fun onClickItem(weatherData: WeatherData) {
        val latLng=LatLng(weatherData.city!!.coord.lat,weatherData.city.coord.lon)
        val intent= Intent(context, MainActivity::class.java)
           .putExtra("selectedLatLng",latLng)
            .putExtra("id","map")
        startActivity(intent)
    }
    fun fahrenheitToCelsius(fahrenheit: Double): Double {
        val result = (fahrenheit - 32) / 1.8
        return String.format("%.2f", result).toDouble()
    }

    fun celsiusToFahrenheit(celsius: Double): Double {
        val result = (celsius * 1.8) + 32
        return String.format("%.2f", result).toDouble()
    }
}