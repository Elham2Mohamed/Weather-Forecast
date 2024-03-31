package com.example.weatherforecastapplication.db

import com.example.weatherforecastapplication.model.WeatherData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception


@Suppress("UNCHECKED_CAST")
class FakeWeatherLocalDataSource(private val tasks:MutableList<WeatherData>?= mutableListOf()) :IWeatherLocalDataSource{
    override suspend fun insertWeather(weatherData: WeatherData) {
        tasks?.add(weatherData)
    }

    override suspend fun deleteWeather(weatherData: WeatherData) {
        tasks?.remove(weatherData)
    }

    override suspend fun getAllWeathers(): Flow<List<WeatherData>> {
        return flow {
            emit(tasks ?: emptyList())
        }
    }

    override suspend fun getWeatherById(id: Long): WeatherData? {
        return if (tasks.isNullOrEmpty() || id >= tasks.size) {
            null
        } else {
            tasks[id.toInt()]
        }
    }
}