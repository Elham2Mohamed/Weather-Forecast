package com.example.weatherforecastapplication.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
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
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale


const val REQUEST_LOCATION_CODE = 2005
const val REQUEST_SMS_PERMISSION_CODE = 1001

class HomeFragment : Fragment() {
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
    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var recyclerView1: RecyclerView
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var layoutManager1: LinearLayoutManager
    private lateinit var recyclerView2: RecyclerView
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var layoutManager2: LinearLayoutManager
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader_view = view.findViewById(R.id.loader)
        tvcity = view.findViewById(R.id.tvCity)
        tvDate = view.findViewById(R.id.tvDate)
        tvDegree = view.findViewById(R.id.tvDegre)
        tvDesc = view.findViewById(R.id.tvDesc)
        tvPressure = view.findViewById(R.id.tvPressrue)
        tvWind = view.findViewById(R.id.tvWin)
        tvHum = view.findViewById(R.id.tvHum)
        tvCloud = view.findViewById(R.id.tvCloud)
        tvTemp = view.findViewById(R.id.tvTemp)
        tvVisibility = view.findViewById(R.id.tvVisibility)
        recyclerView1 = view.findViewById(R.id.recyclerView1)
        recyclerView2 = view.findViewById(R.id.recyclerView2)

        val repository = WeatherRepositoryImp.getInstance(RemoteDataSourceImpl())
        val viewModelFactory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        hourlyAdapter = HourlyAdapter {
            Log.i("TAG", "onViewCreated: ")
        }
        layoutManager1 = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView1.layoutManager = layoutManager1
        recyclerView1.adapter = hourlyAdapter

        dailyAdapter = DailyAdapter {
            Log.i("TAG", "onViewCreated: ")
        }
        layoutManager2 = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView2.layoutManager = layoutManager2
        recyclerView2.adapter = dailyAdapter

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

                        val data = result.data.list.subList(0, 8)
                        hourlyAdapter.submitList(data)

                        val filteredList = mutableListOf<WeatherEntry>()
                        var startIndex = 0
                        while (startIndex < result.data.list.size) {
                            val endIndex = minOf(startIndex + 1, result.data.list.size)
                            filteredList.addAll(result.data.list.subList(startIndex, endIndex))
                            startIndex += 8
                        }
                        dailyAdapter.submitList(filteredList)
                        val dtTxt = result.data.list[0].dt_txt
                        val parts = dtTxt.split(" ")
                        val part = parts[1].split(":")

                        tvcity.text = result.data.city?.name ?: "cairo"
                        if(part[0].compareTo("12")>0)
                            tvDate.text = parts[0]+ " "+ part[0]+" PM"
                        else
                            tvDate.text = parts[0]+ "  "+ part[0]+" AM"

                        tvDegree.text = result.data.list[0].wind.deg.toString() + "°C"
                        tvDesc.text = result.data.list[0].weather[0].description
                        tvPressure.text = result.data.list[0].main.pressure.toString()
                        tvWind.text = result.data.list[0].wind.speed.toString()
                        tvHum.text = result.data.list[0].main.humidity.toString()
                        tvCloud.text = result.data.list[0].clouds.all.toString()
                        tvTemp.text = result.data.list[0].main.temp.toString()
                        tvVisibility.text = result.data.list[0].visibility.toString()
                    }

                    is ApiState.Failure -> {
                        loader_view.visibility = View.GONE
                        recyclerView1.visibility = View.GONE
                        recyclerView2.visibility = View.GONE
                        Log.i("TAG", "onViewCreated: Error")
                    }
                }
            }
        }
        getFreshLocation()
        //viewModel.getCurrentWeather(44.34, 10.99, "7827a54d5a63cea51b327c7a48bf36ec")
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
        val locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {

        if (checkPermissions()) {

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())


            fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 10000
                },
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)

                        val location = locationResult.lastLocation
                        location?.let {
                            viewModel.getCurrentWeather(it.latitude, it.longitude, "7827a54d5a63cea51b327c7a48bf36ec")
                        }
                    }
                },
                Looper.getMainLooper()
            )
        } else {

            requestLocationPermissions()
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

                Toast.makeText(requireContext(), "Location permissions are required", Toast.LENGTH_SHORT).show()
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


}