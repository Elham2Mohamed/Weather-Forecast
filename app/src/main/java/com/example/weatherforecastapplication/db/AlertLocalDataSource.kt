package com.example.weatherforecastapplication.db

import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface AlertLocalDataSource {
    suspend fun insertAlertWeather(alertWeather: AlertWeather)
    suspend fun deleteAlertWeather(alertWeather: AlertWeather)
    suspend fun getAllAlertsWeathers(): Flow<List<AlertWeather>>
    suspend fun getAlertWeatherById(id: Long): AlertWeather?
}