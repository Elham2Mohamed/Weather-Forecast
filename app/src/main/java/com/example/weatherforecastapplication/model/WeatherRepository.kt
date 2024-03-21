package com.example.weatherforecastapplication.model

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double,lang:String,units :String, apiKey: String): Flow<WeatherData>
    suspend fun getCurrentWeather(lat: Double, lon: Double,lang:String, apiKey: String): Flow<WeatherData>
    suspend fun getFAVWeathers(): Flow<List<WeatherData>>
    suspend fun insertWeather(weatherData: WeatherData)
    suspend fun deleteWeather(weatherData: WeatherData)
    suspend fun getWeatherById(id: Long): WeatherData?
}