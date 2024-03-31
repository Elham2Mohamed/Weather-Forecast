package com.example.weatherforecastapplication.db

import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.WeatherData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAlertWeatherLocalDataSource (private val tasks:MutableList<AlertWeather>?= mutableListOf()) :AlertLocalDataSource{
    override suspend fun insertAlertWeather(alertWeather: AlertWeather) {
        tasks?.add(alertWeather)
    }

    override suspend fun deleteAlertWeather(alertWeather: AlertWeather) {
        tasks?.remove(alertWeather)
    }

    override suspend fun getAllAlertsWeathers(): Flow<List<AlertWeather>> {
        return flow {
            emit(tasks ?: emptyList())
        }
    }

    override suspend fun getAlertWeatherById(id: Long): AlertWeather? {
        return if (tasks.isNullOrEmpty() || id >= tasks.size) {
            null
        } else {
            tasks[id.toInt()]
        }
    }
}