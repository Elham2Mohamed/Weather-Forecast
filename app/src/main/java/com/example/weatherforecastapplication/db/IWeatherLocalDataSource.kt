package com.example.weatherforecastapplication.db


import com.example.weatherforecastapplication.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {
    suspend fun insertWeather(weatherData: WeatherData)
    suspend fun deleteWeather(weatherData: WeatherData)
    suspend fun getAllWeathers(): Flow<List<WeatherData>>
}