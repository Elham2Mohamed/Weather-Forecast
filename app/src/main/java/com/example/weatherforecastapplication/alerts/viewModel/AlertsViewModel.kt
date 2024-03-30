package com.example.weatherforecastapplication.alerts.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertsViewModel (private val repo : WeatherRepository):ViewModel() {
    private var _alerts:MutableLiveData<List<AlertWeather>> = MutableLiveData()
    val alerts:LiveData<List<AlertWeather>> =_alerts
    init {
        getLocalAlertsWeather()
    }

    private fun getLocalAlertsWeather() {
        viewModelScope.launch (Dispatchers.IO){
            repo.getAlertWeathers()?.collect{
                _alerts.postValue(it)

            }
        }
    }
    val newAlertItems = mutableListOf<AlertWeather>()


    fun deleteAlertWeather(alertWeather: AlertWeather){
        viewModelScope.launch (Dispatchers.IO){
            repo.deleteAlertWeather(alertWeather)
            getLocalAlertsWeather()
        }
    }
    @SuppressLint("LogNotTimber")
    fun insertAlertWeather(alertWeather: AlertWeather){
        viewModelScope.launch (Dispatchers.IO){
            repo.insertAlertWeather(alertWeather)
            Log.i("AlertsViewModel", "Inserting AlertWeather: $alertWeather")

        }
    }
    suspend fun getAlertWeatherById(id: Long): AlertWeather? {
        return repo.getAlertWeatherById(id)
    }


}
