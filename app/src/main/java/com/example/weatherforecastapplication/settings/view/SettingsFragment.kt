package com.example.weatherforecastapplication.settings.view

import android.content.Context
import android.content.SharedPreferences
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
import com.example.weatherforecastapplication.db.SettingsLocalDataSourceImpl
import com.example.weatherforecastapplication.db.WeatherLocalDataSource
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherRepositoryImp
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModel
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModelFactory

class SettingsFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var Factory: SettingsViewModelFactory
    private lateinit var viewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Factory = SettingsViewModelFactory(
            WeatherRepositoryImp.getInstance(
                RemoteDataSourceImpl(),
                WeatherLocalDataSource(requireContext()),
                SettingsLocalDataSourceImpl(requireContext())
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
        sharedPreferences = requireContext().applicationContext.getSharedPreferences(
            "PREFERENCES",
            Context.MODE_PRIVATE
        )

        val rgLanguages = view.findViewById<RadioGroup>(R.id.rgLanguages)
        val rgLocation = view.findViewById<RadioGroup>(R.id.rgLocation)
        val rgTemperature = view.findViewById<RadioGroup>(R.id.rgTemperature)
        val rgSpeed = view.findViewById<RadioGroup>(R.id.rgSpeed)
        val rgNotifications = view.findViewById<RadioGroup>(R.id.rgNotifications)

        val lang = sharedPreferences.getString("language", "en").toString()
        if (lang == "ar") {
            rgLanguages.check(R.id.rBtn2Languages)
        } else {
            rgLanguages.check(R.id.rBtnLanguages)
        }


        if (viewModel.getLocation() == "gps") {
            rgLanguages.check(R.id.rBtnLocation)
        } else {
            rgLanguages.check(R.id.rBtn2Location)
        }


        if (viewModel.getSpeed() == "meter/sec") {
            rgLanguages.check(R.id.rBtnSpeed)
        } else {
            rgLanguages.check(R.id.rBtn2Speed)
        }


        if (viewModel.getTemp() == "kelvin") {
            rgLanguages.check(R.id.rBtnTemperature)
        } else if (viewModel.getTemp() == "celsius") {
            rgLanguages.check(R.id.rBtn2Temperature)
        } else {
            rgLanguages.check(R.id.rBtn3Temperature)
        }



        rgLanguages.setOnCheckedChangeListener { _, checkedId ->
            Log.i("TAG", "onViewCreated: enter language ")
            val lang = when (checkedId) {
                R.id.rBtnLanguages -> "en"
                R.id.rBtn2Languages -> "ar"
                else -> "en"
            }
            viewModel.setLanguage(lang)
        }

        rgLocation.setOnCheckedChangeListener { _, checkedId ->
            Log.i("TAG", "onViewCreated: enter location ")
            // rgLocation.check(checkedId)
            val location = when (checkedId) {
                R.id.rBtnLocation -> "gps"
                else -> {
                    val navController = findNavController()
                    val action = SettingsFragmentDirections.actionNavigationSettingsToNavigationMap()
                    action.id = 1
                    navController.navigate(action)
                             "map"
                }

            }
            viewModel.setLocation(location)

        }

        rgTemperature.setOnCheckedChangeListener { _, checkedId ->
            //rgTemperature.check(checkedId)

            val temp = when (checkedId) {
                R.id.rBtnTemperature -> "kelvin"
                R.id.rBtn2Temperature -> "celsius"
                R.id.rBtn3Temperature -> "fahrenheit"
                else -> "kelvin"
            }
            viewModel.setTemp(temp)
        }

        rgSpeed.setOnCheckedChangeListener { _, checkedId ->
            // rgSpeed.check(checkedId)

            val speed = when (checkedId) {
                R.id.rBtnSpeed -> "meter/sec"
                R.id.rBtn2Speed -> "miles/hour"
                else -> "meter/sec"
            }
            viewModel.setSpeed(speed)
        }


//        rgNotifications.setOnCheckedChangeListener { _, checkedId ->
//            viewModel.
//
//        }


    }

}
