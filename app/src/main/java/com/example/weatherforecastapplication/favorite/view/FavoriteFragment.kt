package com.example.weatherforecastapplication.favorite.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymvvmapplication.favproduct.viewmodel.FAVWeatherViewModel
import com.example.mymvvmapplication.favproduct.viewmodel.FAVWeatherViewModelFactory
import com.example.weatherforecastapplication.MainActivity
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.databinding.FragmentFavoriteBinding
import com.example.weatherforecastapplication.db.AlertLocalDataSourceImpl
import com.example.weatherforecastapplication.db.SettingsLocalDataSourceImpl
import com.example.weatherforecastapplication.db.WeatherLocalDataSource
import com.example.weatherforecastapplication.home.viewModle.HomeViewModel
import com.example.weatherforecastapplication.home.viewModle.HomeViewModelFactory
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherRepositoryImp
import com.example.weatherforecastapplication.network.ApiState
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModel
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE")
class FavoriteFragment : Fragment(), FavOnClickListener {
    lateinit var btnAdd: FloatingActionButton
    private var _binding: FragmentFavoriteBinding? = null
    lateinit var language: String
    lateinit var speed: String
    lateinit var temp: String
    private lateinit var hviewModel: HomeViewModel
    private lateinit var favFactory: FAVWeatherViewModelFactory
    private lateinit var viewModel: FAVWeatherViewModel
    lateinit var sFactory: SettingsViewModelFactory
    lateinit var sViewModel: SettingsViewModel
    private lateinit var favRecyclerView: RecyclerView
    private lateinit var favAdapter: FAVWeatherAdapter
    private lateinit var layoutManager: GridLayoutManager

    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("SuspiciousIndentation", "LogNotTimber")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favFactory = FAVWeatherViewModelFactory(
            WeatherRepositoryImp.getInstance(
                RemoteDataSourceImpl(),
                WeatherLocalDataSource(requireContext()),
                SettingsLocalDataSourceImpl(requireContext()),
                AlertLocalDataSourceImpl(requireContext())
            )
        )

        viewModel = ViewModelProvider(this, favFactory).get(FAVWeatherViewModel::class.java)

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
        checkStoredData()

        val repository = WeatherRepositoryImp.getInstance(
            RemoteDataSourceImpl(),
            WeatherLocalDataSource(requireContext()),
            SettingsLocalDataSourceImpl(requireContext()),
            AlertLocalDataSourceImpl(requireContext())
        )
        val viewModelFactory = HomeViewModelFactory(repository)
        hviewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        var x = false
        lifecycleScope.launch {
            hviewModel.currentWeather.collect { result ->
                when (result) {
                    is ApiState.Success -> {
                        if (x == true) {
                            if (result.data.list.isNotEmpty()) {
                                viewModel.insertWeather(result.data)
                                x = false
                            } else
                                showInternetConnectionDialog()
                        }
                    }

                    is ApiState.Failure -> {
                        showInternetConnectionDialog()
                    }

                    else -> {
                        Log.i("TAG", "onViewCreated: Loading!!!")
                    }
                }
            }
        }

        if (arguments != null && arguments?.getInt("id") == 2) {
            val latitude = arguments?.getString("lat")!!.toDouble()

            val longitude = arguments?.getString("log")!!.toDouble()
            x = true
            hviewModel.getCurrentWeather(
                latitude,
                longitude,
                language,
                "39b555dfd3fff6499b8c963689da8e9c"
            )

            arguments = null
        }


        favRecyclerView = view.findViewById(R.id.list_item)
        favAdapter = FAVWeatherAdapter(this, this)
        layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        favRecyclerView.layoutManager = layoutManager
        favRecyclerView.adapter = favAdapter


            viewModel.weathres.observe(viewLifecycleOwner) { weatherList ->
                val filteredList = (mutableListOf<WeatherData>())
                   weatherList.forEach{
                       if(it.id != (1).toLong()){
                           filteredList.add(it)
                       }
                   }

            val updatedList = filteredList + viewModel.newFavoriteItems
            favAdapter.submitList(updatedList)
        }
        btnAdd = view.findViewById(R.id.btnAddLoction)
        btnAdd.setOnClickListener {
            val navController = findNavController()
            val action = FavoriteFragmentDirections.actionNavigationFavoriteToNavigationMap()
            action.id = 2
            navController.navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("LogNotTimber")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkStoredData() {
        language = sViewModel.getLanguage()
        Log.i("TAG", "checkStoredData: language :${sViewModel.getLanguage()}")
        speed = sViewModel.getSpeed()
        temp = sViewModel.getTemp()
    }

    override fun deleteItem(weatherData: WeatherData) {
        viewModel.deleteWeather(weatherData)
    }

    override fun onClickItem(weatherData: WeatherData) {
        val latLng = LatLng(weatherData.city!!.coord.lat, weatherData.city.coord.lon)
        val intent = Intent(context, MainActivity::class.java)
            .putExtra("selectedLatLng", latLng)
            .putExtra("id", "map")
            .putExtra("from", "fav")

        startActivity(intent)
    }

    fun convertKelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }

    fun convertKelvinToFahrenheit(kelvin: Double): Double {
        return ((kelvin * 9) / 5) - 459.67
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
}