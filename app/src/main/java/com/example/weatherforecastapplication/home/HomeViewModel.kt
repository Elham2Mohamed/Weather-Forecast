package com.example.weatherforecastapplication.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherRepository
import com.example.weatherforecastapplication.network.ApiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _currentWeather : MutableStateFlow < ApiState > = MutableStateFlow(ApiState.Loading)
    val currentWeather: StateFlow<ApiState> = _currentWeather

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.i("TAG", "Exception HomeViewModel : $exception")
    }

    @SuppressLint("SuspiciousIndentation")
    fun getCurrentWeather(lat: Double, lon: Double, lang:String, units :String, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {

        val weather = repository.getCurrentWeather(lat, lon,lang,units , apiKey)
           weather.catch { e ->
                withContext(Dispatchers.Main) {
                    _currentWeather.value = ApiState.Failure(e)
                }
            }
            .collect {
                withContext(Dispatchers.Main) {
                    _currentWeather.value = ApiState.Success(it)
                }
            }

        }
    }

    fun getCurrentWeather(lat: Double, lon: Double, lang:String, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {

            val weather = repository.getCurrentWeather(lat, lon,lang , apiKey)
            weather.catch { e ->
                withContext(Dispatchers.Main) {
                    _currentWeather.value = ApiState.Failure(e)
                }
            }
                .collect {
                    withContext(Dispatchers.Main) {
                        _currentWeather.value = ApiState.Success(it)
                    }
                }

        }
    }
}