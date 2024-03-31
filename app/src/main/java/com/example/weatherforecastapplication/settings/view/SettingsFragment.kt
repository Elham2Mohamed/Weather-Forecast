package com.example.weatherforecastapplication.settings.view

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.db.AlertLocalDataSourceImpl
import com.example.weatherforecastapplication.db.SettingsLocalDataSourceImpl
import com.example.weatherforecastapplication.db.WeatherLocalDataSource
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherRepositoryImp
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModel
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModelFactory

private const val PREFERENCES = "PREFERENCES"

class SettingsFragment : Fragment() {
    private val LANGUAGE="language"
    private val SPEED="speed"
    private val LOCATION="location"
    private val TEMP="temp"
    private val NOTIFICATION="notification"
    private lateinit var Factory: SettingsViewModelFactory
    private lateinit var viewModel: SettingsViewModel
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Factory = SettingsViewModelFactory(
            WeatherRepositoryImp.getInstance(
                RemoteDataSourceImpl(),
                WeatherLocalDataSource(requireContext()),
                SettingsLocalDataSourceImpl(requireContext()),
                AlertLocalDataSourceImpl(requireContext())
            )
        )

        viewModel = ViewModelProvider(requireActivity(), Factory).get(SettingsViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(
            PREFERENCES, Context.MODE_PRIVATE
        )

        val rgLanguages = view.findViewById<RadioGroup>(R.id.rgLanguages)
        val rgLocation = view.findViewById<RadioGroup>(R.id.rgLocation)
        val rgTemperature = view.findViewById<RadioGroup>(R.id.rgTemperature)
        val rgSpeed = view.findViewById<RadioGroup>(R.id.rgSpeed)
        val rgNotifications = view.findViewById<RadioGroup>(R.id.rgNotifications)

        rgLanguages.check(if (sharedPreferences.getString(LANGUAGE, "en") == "ar") R.id.rBtn2Languages else R.id.rBtnLanguages)
        rgLocation.check(if (sharedPreferences.getString(LOCATION, "gps") == "gps") R.id.rBtnLocation else R.id.rBtn2Location)
        rgSpeed.check(if (sharedPreferences.getString(SPEED, "meter/sec") == "meter/sec") R.id.rBtnSpeed else R.id.rBtn2Speed)
        rgTemperature.check(
            when (sharedPreferences.getString(TEMP, "kelvin")) {
                "celsius" -> R.id.rBtn2Temperature
                "fahrenheit" -> R.id.rBtn3Temperature
                else -> R.id.rBtnTemperature
            }
        )
        rgNotifications.check(if (sharedPreferences.getString(NOTIFICATION, "enable") == "enable") R.id.rBtnNotifications else R.id.rBtn2Notifications)


        rgLanguages.setOnCheckedChangeListener { _, checkedId ->

            val lang = when (checkedId) {
                R.id.rBtnLanguages -> "en"
                R.id.rBtn2Languages -> "ar"
                else -> "en"
            }
            viewModel.setLanguage(lang)
        }

        rgLocation.setOnCheckedChangeListener { _, checkedId ->
            val location = when (checkedId) {
                R.id.rBtnLocation -> "gps"
                else -> {
                    val navController = findNavController()
                    val action =
                        SettingsFragmentDirections.actionNavigationSettingsToNavigationMap()
                    action.id = 1
                    navController.navigate(action)
                    "map"
                }
            }
            viewModel.setLocation(location)

        }

        rgTemperature.setOnCheckedChangeListener { _, checkedId ->

            val temp = when (checkedId) {
                R.id.rBtnTemperature -> "kelvin"
                R.id.rBtn2Temperature -> "celsius"
                R.id.rBtn3Temperature -> "fahrenheit"
                else -> "kelvin"
            }
            viewModel.setTemp(temp)
        }

        rgSpeed.setOnCheckedChangeListener { _, checkedId ->

            val speed = when (checkedId) {
                R.id.rBtnSpeed -> "meter/sec"
                R.id.rBtn2Speed -> "miles/hour"
                else -> "meter/sec"
            }
            viewModel.setSpeed(speed)
        }

        rgNotifications.setOnCheckedChangeListener { _, checkedId ->
            val notification = when (checkedId) {
                R.id.rBtnNotifications -> "enable"
                R.id.rBtn2Notifications -> "disable"
                else -> "enable"
            }
            viewModel.setNotificationAccess(notification)
        }


    }
    private fun isConnectedToInternet(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}
