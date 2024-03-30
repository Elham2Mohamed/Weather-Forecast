package com.example.weatherforecastapplication.model

import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherEntry
import com.example.weatherforecastapplication.network.RemoteDataSource
import com.example.weatherforecastapplication.network.RetrofitHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSourceImpl : RemoteDataSource {

    override suspend fun getCurrentWeather(lat: Double, lon: Double,lang:String,apiKey: String): Flow<WeatherData> {
        return flow {
            try {
                val weatherData = RetrofitHelper.weatherService.getCurrentWeatherWithoutUints(lat, lon,lang,apiKey)
                emit(weatherData)
            } catch (e: Exception) {
                emit(WeatherData(0," ",0,0, emptyList<WeatherEntry>(),null)) // You need to provide a default value or handle the error accordingly
            }
        }
    }
}
