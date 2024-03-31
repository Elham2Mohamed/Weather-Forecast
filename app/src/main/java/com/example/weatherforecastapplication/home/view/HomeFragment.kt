package com.example.weatherforecastapplication.home.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymvvmapplication.favproduct.viewmodel.FAVWeatherViewModel
import com.example.mymvvmapplication.favproduct.viewmodel.FAVWeatherViewModelFactory
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.databinding.FragmentHomeBinding
import com.example.weatherforecastapplication.db.AlertLocalDataSourceImpl
import com.example.weatherforecastapplication.db.SettingsLocalDataSourceImpl
import com.example.weatherforecastapplication.db.WeatherLocalDataSource
import com.example.weatherforecastapplication.home.viewModle.HomeViewModel
import com.example.weatherforecastapplication.home.viewModle.HomeViewModelFactory
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherEntry
import com.example.weatherforecastapplication.model.WeatherRepositoryImp
import com.example.weatherforecastapplication.network.ApiState
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModel
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.log


const val REQUEST_LOCATION_CODE = 2005
private const val PREFERENCES = "HOMEPREFERENCES"

class HomeFragment : Fragment() {
    lateinit var image: String
    private lateinit var favFactory: FAVWeatherViewModelFactory
    private lateinit var fviewModel: FAVWeatherViewModel
    private lateinit var loader_view: ProgressBar
    private lateinit var tvcity: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvDegree: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvPressure: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvHum: TextView
    private lateinit var tvCloud: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvVisibility: TextView
    private lateinit var weatherData: WeatherData
    private lateinit var icons: ImageView
    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var sFactory: SettingsViewModelFactory
    lateinit var sViewModel: SettingsViewModel
    private lateinit var recyclerView1: RecyclerView
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var layoutManager1: LinearLayoutManager
    private lateinit var recyclerView2: RecyclerView
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var layoutManager2: LinearLayoutManager
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var language: String
    lateinit var speed: String
    lateinit var temp: String
    lateinit var date: LocalDate
    var isFirst = false

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val LANGUAGE = "language"
    private val LONGITUDE = "longitude"
    private val LATITUDE = "latitude"
    private val DATE = "date"

    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    @SuppressLint("LogNotTimber", "SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(
            PREFERENCES, Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()
        setupViews()
        setupViewModel()
        setupAdapters()
        checkStoredData()

        if (isConnectedToInternet()) {
            if (isFirst == true &&  sharedPreferences.getString(LANGUAGE, "en")
                    .toString() == sViewModel.getLanguage() && sharedPreferences.getString(
                    LATITUDE,
                    "0.0"
                )!!.toDouble() == sViewModel.getLatitude() && sharedPreferences.getString(
                    LONGITUDE,
                    "0.0"
                )!!.toDouble() == sViewModel.getLongitude() && sharedPreferences.getString(
                    DATE,
                    ""
                ) == sViewModel.getDate()
            ) {
                lifecycleScope.launch(Dispatchers.IO) {
                    Log.i(
                        "TAG",
                        "onViewCreated: lang : ${
                            sharedPreferences.getString(LANGUAGE, "en").toString()
                        } ,lat: ${
                            sharedPreferences.getString(LATITUDE, "0.0")!!.toDouble()
                        } ,lng : ${
                            sharedPreferences.getString(LONGITUDE, "0.0")!!.toDouble()
                        }, date ${sharedPreferences.getString(DATE, "")}}"
                    )

                    Log.i(
                        "TAG",
                        "onViewCreated: STRO location : ${sViewModel.getLocation()} ,lang : ${sViewModel.language} ,lat: ${sViewModel.getLatitude()} ,lng : ${sViewModel.getLongitude()}, temp :${sViewModel.getTemp()}, speed : ${sViewModel.getSpeed()}}"
                    )

                    val weather = fviewModel.getWeatherById((1).toLong())
                    if (weather != null) {
                        Log.i(
                            "TAG",
                            "onViewCreated: Room  lang : ${
                                sharedPreferences.getString(LANGUAGE, "en").toString()
                            } ,lat: ${weather.city!!.coord.lat} ,lng : ${weather.city.coord.lon}, date ${
                                sharedPreferences.getString(
                                    DATE,
                                    ""
                                )
                            }}"
                        )
                        val image1 = (weather!!.list[0].weather[0].icon)
                        image = image1
                        handleWeather(weather, image1)

                    } else
                        Log.i("TAG", "onViewCreated:  room return null")
                }
            } else {
                Log.i(
                    "TAG",
                    "onViewCreated: lang : ${
                        sharedPreferences.getString(LANGUAGE, "en").toString()
                    } ,lat: ${
                        sharedPreferences.getString(LATITUDE, "0.0")!!.toDouble()
                    } ,lng : ${
                        sharedPreferences.getString(LONGITUDE, "0.0")!!.toDouble()
                    }, date ${sharedPreferences.getString(DATE, "")}}"
                )
                Log.i(
                    "TAG",
                    "onViewCreated: API  location : ${sViewModel.getLocation()} ,lang : ${sViewModel.language} ,lat: ${sViewModel.getLatitude()} ,lng : ${sViewModel.getLongitude()}, temp :${sViewModel.getTemp()}, speed : ${sViewModel.getSpeed()},date : ${sViewModel.getDate()}}"
                )
                var isConnectedToInternet = true
                lifecycleScope.launch {
                    viewModel.currentWeather.collect { result ->
                        isFirst=true
                        when (result) {
                            is ApiState.Loading -> {
                                loader_view.visibility = View.VISIBLE
                                recyclerView1.visibility = View.GONE
                                recyclerView2.visibility = View.GONE
                            }

                            is ApiState.Success -> {
                                loader_view.visibility = View.GONE
                                recyclerView1.visibility = View.VISIBLE
                                recyclerView2.visibility = View.VISIBLE
                                weatherData = result.data

                                if (weatherData.list.isNotEmpty()) {
                                    val image1 = (weatherData.list[0].weather[0].icon)
                                    image = image1
                                    val dtTxt = weatherData.list[0].dt_txt
                                    val parts = dtTxt.split(" ")

                                    val weatherDataWithIdOne = weatherData.copy(id = 1)
                                    fviewModel.insertWeather(weatherDataWithIdOne)

                                    editor.putString(LANGUAGE, sViewModel.getLanguage())
                                    editor.putString(LATITUDE, sViewModel.getLatitude().toString())
                                    editor.putString(
                                        LONGITUDE,
                                        sViewModel.getLongitude().toString()
                                    )
                                    editor.putString(DATE,dtTxt)
                                    editor.apply()

                                    handleWeather(weatherData, image1)
                                } else {
                                    withContext(Dispatchers.Main) {
                                        if (isConnectedToInternet == true) {
                                            runBlocking() {
                                                isConnectedToInternet = false
                                                showInternetConnectionDialog()

                                            }
                                        }
                                        Log.i("TAG", "onViewCreated: Error no weatherData")
                                    }
                                }
                            }

                            else -> {
                                if (fviewModel.getWeatherById(1) != null) {
                                    weatherData = fviewModel.getWeatherById(1)!!
                                    val image1 = (weatherData.list[0].weather[0].icon)
                                    image = image1
                                    handleWeather(weatherData, image1)
                                } else {
                                    withContext(Dispatchers.Main) {
                                        showInternetConnectionDialog()
                                    }
                                    loader_view.visibility = View.GONE
                                    recyclerView1.visibility = View.GONE
                                    recyclerView2.visibility = View.GONE

                                    Log.i("TAG", "onViewCreated: Error")
                                }
                            }

                        }
                    }
                }
                getFreshLocation()
            }
        } else {
            showInternetConnectionDialog()
        }
    }


    private fun isConnectedToInternet(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    private fun showInternetConnectionDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.network_daialog)
        val btnOK = dialog.findViewById<Button>(R.id.btnOk)
        btnOK.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkStoredData() {
        language = sViewModel.getLanguage()
        speed = sViewModel.getSpeed()
        temp = sViewModel.getTemp()
    }

    private fun setupViewModel() {
        sFactory = SettingsViewModelFactory(
            WeatherRepositoryImp.getInstance(
                RemoteDataSourceImpl(),
                WeatherLocalDataSource(requireContext()),
                SettingsLocalDataSourceImpl(requireContext()),
                AlertLocalDataSourceImpl(requireContext())
            )
        )

        sViewModel =
            ViewModelProvider(requireActivity(), sFactory).get(SettingsViewModel::class.java)

        val repository = WeatherRepositoryImp.getInstance(
            RemoteDataSourceImpl(),
            WeatherLocalDataSource(requireContext()), SettingsLocalDataSourceImpl(requireContext()),
            AlertLocalDataSourceImpl(requireContext())
        )
        val viewModelFactory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        favFactory = FAVWeatherViewModelFactory(
            WeatherRepositoryImp.getInstance(
                RemoteDataSourceImpl(),
                WeatherLocalDataSource(requireContext()),
                SettingsLocalDataSourceImpl(requireContext()),
                AlertLocalDataSourceImpl(requireContext())
            )
        )

        fviewModel = ViewModelProvider(this, favFactory).get(FAVWeatherViewModel::class.java)
    }

    private fun setupAdapters() {
        hourlyAdapter = HourlyAdapter(this)
        layoutManager1 = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.recyclerView1.layoutManager = layoutManager1
        binding.recyclerView1.adapter = hourlyAdapter

        dailyAdapter = DailyAdapter(this)
        layoutManager2 = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView2.layoutManager = layoutManager2
        binding.recyclerView2.adapter = dailyAdapter
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleWeather(weatherData: WeatherData, image: String) {

        weatherData ?: return
        val icon = getImage(image)
        icons.setImageResource(icon)

        getHourlyWeather(weatherData)
        getDailyWeather(weatherData)
        lifecycleScope.launch {

            tvcity.text = weatherData.city?.name ?: "cairo"

            if (weatherData.city?.name == "Mountain View")
                tvcity.text = "Cairo"
            else if (weatherData.city?.name == "ماونتن فيو")
                tvcity.text = "القاهرة"
            tvDesc.text = weatherData.list[0].weather[0].description
            tvPressure.text = weatherData.list[0].main.pressure.toString() + " hpa"
            tvHum.text = weatherData.list[0].main.humidity.toString() + " %"
            tvCloud.text = weatherData.list[0].clouds.all.toString() + " %"
            tvVisibility.text = weatherData.list[0].visibility.toString() + " m"
        }
        changeTime(weatherData)
        changeTempAndSpeed(weatherData)

    }

    private fun getDailyWeather(weatherData: WeatherData) {
        val filteredList = mutableListOf<WeatherEntry>()
        var startIndex = 0
        while (startIndex < weatherData.list.size) {
            val endIndex = minOf(startIndex + 1, weatherData.list.size)
            filteredList.addAll(weatherData.list.subList(startIndex, endIndex))
            startIndex += 8
        }
        lifecycleScope.launch(Dispatchers.Main) {
            dailyAdapter.submitList(filteredList)
        }
    }

    private fun getHourlyWeather(weatherData: WeatherData) {
        val data = weatherData.list.subList(0, 8)
        lifecycleScope.launch(Dispatchers.Main) {
            hourlyAdapter.submitList(data)
        }

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun changeTime(weatherData: WeatherData) {

        val dtTxt = weatherData.list[0].dt_txt
        val parts = dtTxt.split(" ")
        val part = parts[1].split(":")
        sViewModel.setDate(dtTxt)

        val dateString = parts[0]
        val formatter = if (language == "ar") {
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale("ar"))
        } else {
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        }
        val date = LocalDate.parse(dateString, formatter)
        this.date = date
        val dayOfWeek = date.dayOfWeek
        val formattedDate = if (language == "ar") {
            if (part[0].compareTo("12") > 0)
                "$dayOfWeek      ${part[0]}:00 مساءً\n ${date}"
            else
                "$dayOfWeek       ${part[0]}:00 صباحًا\n${date}"
        } else {
            if (part[0].compareTo("12") > 0)
                "$dayOfWeek      ${part[0]}:00 PM\n ${parts[0]}"
            else
                "$dayOfWeek       ${part[0]}:00 AM\n${parts[0]}"
        }
        lifecycleScope.launch {
            tvDate.text = formattedDate
        }

    }


    @SuppressLint("SetTextI18n")
    private fun changeTempAndSpeed(weatherData: WeatherData) {
        lifecycleScope.launch {
            when {
                speed == "miles/hour" -> {
                    val result = (weatherData.list[0].wind.speed * 2.24)
                    tvWind.text = "${String.format("%.2f", result).toDouble()}  m/h"
                }

                else -> {
                    tvWind.text = ((weatherData.list[0].wind.speed)).toInt().toString() + " m/s"
                }
            }

            when {
                temp == "fahrenheit" -> {
                    tvDegree.text = convertKelvinToFahrenheit(weatherData.list[0].main.temp).toInt()
                        .toString() + "°F"
                    tvTemp.text = convertKelvinToFahrenheit(weatherData.list[0].main.temp).toInt()
                        .toString() + "°F"

                }

                temp == "celsius" -> {
                    tvDegree.text = convertKelvinToCelsius(weatherData.list[0].main.temp).toInt()
                        .toString() + "°C"
                    tvTemp.text = convertKelvinToCelsius(weatherData.list[0].main.temp).toInt()
                        .toString() + "°C"
                }

                else -> {
                    tvDegree.text = weatherData.list[0].main.temp.toString() + "°K"
                    tvTemp.text = weatherData.list[0].main.temp.toString() + "°K"

                }

            }
        }
    }

    private fun setupViews() {
        loader_view = binding.loader
        tvcity = binding.tvCity
        tvDate = binding.tvDate
        tvDegree = binding.tvDegre
        tvDesc = binding.tvDesc
        tvPressure = binding.tvPressrue
        tvWind = binding.tvWin
        tvHum = binding.tvHum
        tvCloud = binding.tvCloud
        tvTemp = binding.tvTemp
        tvVisibility = binding.tvVisibility
        recyclerView1 = binding.recyclerView1
        recyclerView2 = binding.recyclerView2
        icons = binding.icon
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getFreshLocation()

            } else {
                enableLocationServices()
            }
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_LOCATION_CODE
                )
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleLocation(location: Location) {

        var isFavMap = false
        arguments?.let { args ->
            if (args.containsKey("selectedLatLng") && args.containsKey("fav")) {
                val selectedLatLng = args.getParcelable<LatLng>("selectedLatLng")
                isFavMap = true
                selectedLatLng?.let {
                    viewModel.getCurrentWeather(
                        selectedLatLng.latitude,
                        selectedLatLng.longitude,
                        language,
                        "39b555dfd3fff6499b8c963689da8e9c"
                    )

                }
            }

        }
        if (isFavMap == false) {
            if (sViewModel.getLocation() == "gps") {
                handleGps(location)
            } else {
                if (arguments != null) {
                    val argLatitude = arguments?.getString("lat")!!.toDouble()
                    val argLongitude = arguments?.getString("log")!!.toDouble()

                    val longitude = if (argLongitude != null) {
                        sViewModel.setLongitude(argLongitude)
                        argLongitude
                    } else {
                        sViewModel.getLongitude()
                    }

                    val latitude = if (argLatitude != null) {
                        sViewModel.setLongitude(argLatitude)
                        argLatitude
                    } else {
                        sViewModel.getLatitude()
                    }
                    handleMap(LatLng(longitude, latitude))
                    arguments = null
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleMap(location: LatLng) {
        viewModel.getCurrentWeather(
            location.latitude,
            location.longitude,
            language,
            "39b555dfd3fff6499b8c963689da8e9c"
        )
    }


    private val locationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            location?.let {
                handleLocation(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleGps(location: Location) {
        sViewModel.setLongitude(location.longitude)
        sViewModel.setLatitude(location.latitude)

        viewModel.getCurrentWeather(
            location.latitude,
            location.longitude,
            language,
            "39b555dfd3fff6499b8c963689da8e9c"
        )
    }


    private fun enableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        if (checkPermissions()) {
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 10000
                },
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            requestLocationPermissions()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val location = getCurrentLocation()
                if (location != null) {
                    handleLocation(location)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Unable to retrieve current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permissions are required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    fun getCurrentLocation(): Location? {
        if (checkLocationPermissions()) {
            val locationManager: LocationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }
            val locationGPS: Location? =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val locationNetwork: Location? =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            return if (locationGPS != null && locationNetwork != null) {
                if (locationGPS.time > locationNetwork.time) locationGPS else locationNetwork
            } else if (locationGPS != null) {
                locationGPS
            } else {
                locationNetwork
            }
        }
        return null
    }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_CODE
        )
    }

    private fun checkPermissions(): Boolean {
        return checkLocationPermissions() ||
                (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun checkLocationPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }


    fun getImage(image: String): Int {
        when (image) {
            "01d" -> return R.drawable.cleard
            "01n" -> return R.drawable.clearn
            "02d" -> return R.drawable.fewn
            "20n" -> return R.drawable.fewd
            "10d" -> return R.drawable.raind
            "10n" -> return R.drawable.rainn
            "03d", "03n" -> return R.drawable.scatteredd
            "04d", "04n" -> return R.drawable.brokenn
            "09d", "09n" -> return R.drawable.showerd
            "11d", "11n" -> return R.drawable.thunderstormn
            "13d", "13n" -> return R.drawable.snowd
            "50d", "50n" -> return R.drawable.mistn
        }
        return R.drawable.cloudy
    }


    fun convertKelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }

    fun convertKelvinToFahrenheit(kelvin: Double): Double {
        return ((kelvin * 9) / 5) - 459.67
    }

}


