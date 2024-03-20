package com.example.mymvvmapplication.favproduct.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplication.model.Weather
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FAVWeatherViewModel (private val repo :WeatherRepository):ViewModel() {
    private var _weathre:MutableLiveData<List<WeatherData>> = MutableLiveData()
    val weathres:LiveData<List<WeatherData>> =_weathre
    init {
        getLocalWeathers()
    }

    private fun getLocalWeathers() {
        viewModelScope.launch (Dispatchers.IO){
            repo.getFAVWeathers().collect{
                _weathre.postValue(it)
            }
            }
    }
    fun deleteProduct(weather: WeatherData){
        viewModelScope.launch (Dispatchers.IO){
            repo.deleteWeather(weather)
            getLocalWeathers()
        }
    }
    fun insertWeather(weather: WeatherData){
        viewModelScope.launch (Dispatchers.IO){
            repo.insertWeather(weather)
        }
    }


}

