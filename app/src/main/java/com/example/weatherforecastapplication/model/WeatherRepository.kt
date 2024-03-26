package com.example.weatherforecastapplication.model

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double,lang:String,units :String, apiKey: String): Flow<WeatherData>
    suspend fun getCurrentWeather(lat: Double, lon: Double,lang:String, apiKey: String): Flow<WeatherData>
    suspend fun getFAVWeathers(): Flow<List<WeatherData>>
    suspend fun insertWeather(weatherData: WeatherData)
    suspend fun deleteWeather(weatherData: WeatherData)
    suspend fun getWeatherById(id: Long): WeatherData?
    abstract fun setLanguage(lang: String)
    abstract  fun setTemp(temp:String)
    abstract  fun setUnit(unit:String)
    abstract  fun setSpeed(speed:String)
    abstract  fun setLocation(location:String)
    abstract  fun setLongitude(lng:Double)
    abstract  fun setLatitude(lat:Double)

    abstract  fun getTemp():String

    abstract  fun getLanguage():String
    abstract  fun getUnit():String
    abstract  fun getSpeed():String
    abstract  fun getLocation():String
    abstract  fun getLongitude():Double
    abstract  fun getLatitude():Double
}