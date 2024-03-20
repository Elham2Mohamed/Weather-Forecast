package com.example.mymvvmapplication.favproduct.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapplication.model.WeatherRepository

class FAVWeatherViewModelFactory(private  val repo:WeatherRepository):ViewModelProvider.Factory {
   override fun <T:ViewModel> create(modelClass: Class<T>):T{
        return if(modelClass.isAssignableFrom(FAVWeatherViewModel::class.java)) {
            FAVWeatherViewModel(repo) as T
        }
        else{
            throw IllegalArgumentException("ViewModel Class not found")
       }

    }
}

