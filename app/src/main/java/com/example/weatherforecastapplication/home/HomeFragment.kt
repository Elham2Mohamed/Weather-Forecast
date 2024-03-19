package com.example.weatherforecastapplication.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.databinding.FragmentHomeBinding
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherEntry
import com.example.weatherforecastapplication.model.WeatherRepositoryImp
import com.example.weatherforecastapplication.network.ApiState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


const val REQUEST_LOCATION_CODE = 2005

class HomeFragment : Fragment() {
    lateinit var image: String
    lateinit var sharedPreferences: SharedPreferences
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
//lateinit var args

    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPreferences =
            requireActivity().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        checkStoredData(sharedPreferences)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()
        setupAdapters()

        lifecycleScope.launch {
            viewModel.currentWeather.collect { result ->
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

                        // Log.i("TAG", "handleWeather: icone ${weatherData.list[0].weather[0].icon} ")
                        val image1 = (weatherData.list[0].weather[0].icon)
                        image = image1
                        handleWeather(weatherData, image1)

                    }

                    else -> {
                        loader_view.visibility = View.GONE
                        recyclerView1.visibility = View.GONE
                        recyclerView2.visibility = View.GONE
                        Log.i("TAG", "onViewCreated: Error")
                    }
                }
            }
        }

        getFreshLocation()
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

    private fun setupViewModel() {
        val repository = WeatherRepositoryImp.getInstance(RemoteDataSourceImpl())
        val viewModelFactory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

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
        val icon = getImage(image)
        icons.setImageResource(icon)
//        lifecycleScope.launch {
//            val bitmap = download(image)
//            Log.i("TAG", "handleWeather: image $image")
//            icons.setImageBitmap(bitmap)
//            //Picasso.get().load(bitmap.toString()).into(icons)
//        }
        getHourlyWeather()
        getDailyWeather()
        tvcity.text = weatherData.city?.name ?: "cairo"
        //Log.i("TAG", "handleWeather: ${ weatherData.city?.name}")
        if (weatherData.city?.name == "Mountain View")
            tvcity.text = "Cairo"
        else if (weatherData.city?.name == "ماونتن فيو")
            tvcity.text = "القاهرة"
        tvDesc.text = weatherData.list[0].weather[0].description
        tvPressure.text = weatherData.list[0].main.pressure.toString() + " hpa"
        tvHum.text = weatherData.list[0].main.humidity.toString() + " %"
        tvCloud.text = weatherData.list[0].clouds.all.toString() + " %"
        tvVisibility.text = weatherData.list[0].visibility.toString() + " m"
        changeTime()
        changeTempAndSpeed()

    }

    private fun getDailyWeather() {
        val filteredList = mutableListOf<WeatherEntry>()
        var startIndex = 0
        while (startIndex < weatherData.list.size) {
            val endIndex = minOf(startIndex + 1, weatherData.list.size)
            filteredList.addAll(weatherData.list.subList(startIndex, endIndex))
            startIndex += 8
        }
        dailyAdapter.submitList(filteredList)

    }

    private fun getHourlyWeather() {
        val data = weatherData.list.subList(0, 8)
        hourlyAdapter.submitList(data)

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun changeTime() {

        val dtTxt = weatherData.list[0].dt_txt
        val parts = dtTxt.split(" ")
        val part = parts[1].split(":")


        val dateString = parts[0]
        val formatter = if (language == "ar") {
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale("ar"))
        } else {
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        }
        val date = LocalDate.parse(dateString, formatter)

        val dayOfWeek = date.dayOfWeek
        // Set the date string based on the selected language
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

        // Set the formatted date string to tvDate
        tvDate.text = formattedDate

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun changeLanguage(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resources = resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        getString(R.string.title_home)
        getString(R.string.title_settings)


    }

    @SuppressLint("SetTextI18n")
    private fun changeTempAndSpeed() {
        when {
            speed == "meter/sec" && temp == "Fahrenheit" -> {
                tvDegree.text = celsiusToFahrenheit(weatherData.list[0].main.temp).toString() + "°F"
                tvTemp.text = celsiusToFahrenheit(weatherData.list[0].main.temp).toString() + "°F"
                tvWind.text = weatherData.list[0].wind.speed.toString() + " m/s"
            }

            speed == "miles/hour" && temp == "Celsius" -> {
                tvDegree.text = fahrenheitToCelsius(weatherData.list[0].main.temp).toString() + "°C"
                tvTemp.text = fahrenheitToCelsius(weatherData.list[0].main.temp).toString() + "°C"
                tvWind.text = weatherData.list[0].wind.speed.toString() + " m/h"
            }

            speed == "miles/hour" && temp == "Kelvin" -> {
                tvDegree.text = weatherData.list[0].main.temp.toString() + "°K"
                val result = (weatherData.list[0].wind.speed * 2.24)
                tvWind.text = "${String.format("%.2f", result).toDouble()}  m/h"
                tvTemp.text = weatherData.list[0].main.temp.toString() + "°K"
            }

            speed == "miles/hour" && temp == "Fahrenheit" -> {
                tvDegree.text = weatherData.list[0].main.temp.toString() + "°F"
                tvTemp.text = weatherData.list[0].main.temp.toString() + "°F"
                tvWind.text = weatherData.list[0].wind.speed.toString() + " m/h"
            }

            (speed == "meter/sec" && temp == "Celsius") -> {
                tvDegree.text = weatherData.list[0].main.temp.toString() + "°C"
                tvTemp.text = weatherData.list[0].main.temp.toString() + "°C"
                tvWind.text = weatherData.list[0].wind.speed.toString() + " m/s"
            }

            else -> {
                tvDegree.text = weatherData.list[0].main.temp.toString() + "°K"
                tvTemp.text = weatherData.list[0].main.temp.toString() + "°K"
                tvWind.text = weatherData.list[0].wind.speed.toString() + " m/s"

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

    private fun enableLocationServices() {
        Log.i("TAG", "enableLocationServices: Turn on location ")
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
    private fun handleLocation(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        val units = getUnits()
        changeLanguage(language)
        var isMap = false
        arguments?.let { args ->
            if (args.containsKey("selectedLatLng")) {
                val selectedLatLng = args.getParcelable<LatLng>("selectedLatLng")

                selectedLatLng?.let {
                    isMap = true
                    if (units == "null")
                        viewModel.getCurrentWeather(
                            selectedLatLng.latitude,
                            selectedLatLng.longitude,
                            language,
                            "39b555dfd3fff6499b8c963689da8e9c"
                        )
                    else
                        viewModel.getCurrentWeather(
                            selectedLatLng.latitude,
                            selectedLatLng.longitude,
                            language,
                            units,
                            "39b555dfd3fff6499b8c963689da8e9c"
                        )
                }
            }

        }
        if (!isMap) {
            if (units == "null")
                viewModel.getCurrentWeather(
                    latitude,
                    longitude,
                    language,
                    "39b555dfd3fff6499b8c963689da8e9c"
                )
            else
                viewModel.getCurrentWeather(
                    latitude,
                    longitude,
                    language,
                    units,
                    "39b555dfd3fff6499b8c963689da8e9c"
                )
        }

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

    private fun requestLocationPermissions() {

        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFreshLocation()
            } else {

                Toast.makeText(
                    requireContext(),
                    "Location permissions are required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ((ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED))
    }

    fun fahrenheitToCelsius(fahrenheit: Double): Double {
        val result = (fahrenheit - 32) / 1.8
        return String.format("%.2f", result).toDouble()
    }

    fun celsiusToFahrenheit(celsius: Double): Double {
        val result = (celsius * 1.8) + 32
        return String.format("%.2f", result).toDouble()
    }

    private suspend fun download(url: String): Bitmap? = withContext(Dispatchers.Default) {
        var bitmap: Bitmap? = null
        try {
            bitmap = Picasso.get().load(url).get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        bitmap
    }

    fun getImage(image: String): Int {
        when (image) {
            "01d" -> return R.drawable.cleard
            "01n" -> return R.drawable.clearn
            "02d" -> return R.drawable.fewd
            "20n" -> return R.drawable.fewn
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

    fun onLocationSelected(latLng: LatLng) {
        Log.i(
            "TAG",
            "onLocationSelected:latitude=  ${latLng.latitude} , longitude= ${latLng.longitude}"
        )
    }
}