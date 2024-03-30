package com.example.mymvvmapplication.favproduct.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapplication.alerts.viewModel.AlertsViewModel
import com.example.weatherforecastapplication.model.WeatherRepository

class AlertWeatherViewModelFactory(private  val repo:WeatherRepository):ViewModelProvider.Factory {
   override fun <T:ViewModel> create(modelClass: Class<T>):T{
        return if(modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            AlertsViewModel(repo) as T
        }
        else{
            throw IllegalArgumentException("ViewModel Class not found")
       }

    }
}

