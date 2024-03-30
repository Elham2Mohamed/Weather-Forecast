package com.example.weatherforecastapplication.db

import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.WeatherData
import kotlinx.coroutines.flow.Flow

class FakeAlertWeatherLocalDataSource (private val tasks:MutableList<AlertWeather>?= mutableListOf()) :AlertLocalDataSource{
    override suspend fun insertAlertWeather(alertWeather: AlertWeather) {
        tasks?.add(alertWeather)
    }

    override suspend fun deleteAlertWeather(alertWeather: AlertWeather) {
        tasks?.remove(alertWeather)
    }

    override suspend fun getAllAlertsWeathers(): Flow<List<AlertWeather>> {
        return tasks as Flow<List<AlertWeather>>
    }

    override suspend fun getAlertWeatherById(id: Long): AlertWeather? {
        return tasks?.get(id.toInt())
    }
}