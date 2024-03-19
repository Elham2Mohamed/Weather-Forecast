package com.example.weatherforecastapplication.network

import retrofit2.http.Query
import com.example.weatherforecastapplication.model.WeatherData
import retrofit2.http.GET

interface WeatherService {
    @GET("forecast")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("apiKey") apiKey: String
    ): WeatherData
    @GET("forecast")
    suspend fun getCurrentWeatherWithoutUints(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("apiKey") apiKey: String
    ): WeatherData
}