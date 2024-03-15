package com.example.weatherforecastapplication.model

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String): Flow<WeatherData>
}