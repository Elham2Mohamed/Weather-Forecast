package com.example.weatherforecastapplication.settings.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplication.model.WeatherRepository
import com.example.weatherforecastapplication.network.ApiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _lang: MutableStateFlow<String> = MutableStateFlow<String>(repository.getLanguage())
    val language: StateFlow<String> = _lang

    fun setLanguage(lang: String) {
        viewModelScope.launch{
            _lang.value = lang
            repository.setLanguage(lang)
        }


    }

    fun setTemp(temp: String) {
        repository.setTemp(temp)
    }

    fun setNotificationAccess(access: String) {
        repository.setNotificationAccess(access)
    }

    fun setDate(date: String) {
        repository.setDate(date)
    }

    fun setSpeed(speed: String) {
        repository.setSpeed(speed)
    }

    fun setLocation(location: String) {
        repository.setLocation(location)
    }

    fun setLongitude(lng: Double) {
        repository.setLongitude(lng)
    }

    fun setLatitude(lat: Double) {
        repository.setLatitude(lat)
    }

    fun getTemp(): String {
        return repository.getTemp()
    }

    fun getNotificationAccess(): String {
        return repository.getNotificationAccess()
    }

    fun getLanguage(): String {
        return repository.getLanguage()
    }

    fun getDate(): String {
        return repository.getDate()
    }

    fun getSpeed(): String {
        return repository.getSpeed()
    }

    fun getLocation(): String {
        return repository.getLocation()
    }

    fun getLongitude(): Double {
        return repository.getLongitude()
    }

    fun getLatitude(): Double {
        return repository.getLatitude()
    }

}