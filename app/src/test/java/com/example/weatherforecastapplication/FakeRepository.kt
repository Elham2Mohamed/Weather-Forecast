package com.example.testing_day1.data.source

import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherRepository
import kotlinx.coroutines.flow.Flow

class FakeRepository(private val remoteDataSource: RemoteDataSourceImpl,
                     private var weatherDao: MutableList<WeatherData>,
                     private var settingsDao: List<WeatherData>,
                     private var alertWeatherDao: MutableList<AlertWeather>
): WeatherRepository {
    override suspend fun getAlertWeathers(): Flow<List<AlertWeather>>? {
        return null
    }

    override suspend fun insertAlertWeather(alertWeather: AlertWeather) {
        alertWeatherDao.add(alertWeather)
    }

    override suspend fun deleteAlertWeather(alertWeather: AlertWeather) {
        val indexToRemove = alertWeatherDao.indexOfFirst { it.id == alertWeather.id }
        if (indexToRemove != -1) {
            alertWeatherDao.removeAt(indexToRemove)
        }
    }

    override suspend fun getAlertWeatherById(id: Long): AlertWeather? {
        return alertWeatherDao.get(id.toInt())
    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        apiKey: String
    ): Flow<WeatherData> {
        return remoteDataSource.getCurrentWeather(lat, lon, lang, apiKey)
    }

    override suspend fun getFAVWeathers(): Flow<List<WeatherData>>? {
        return null
    }

    override suspend fun insertWeather(weatherData: WeatherData) {
        weatherDao.add(weatherData)
    }

    override suspend fun deleteWeather(weatherData: WeatherData) {
        val indexToRemove = weatherDao.indexOfFirst { it.id == weatherData.id }
        if (indexToRemove != -1) {
            weatherDao.removeAt(indexToRemove)
        }}

    override suspend fun getWeatherById(id: Long): WeatherData? {
        return weatherDao.get(id.toInt())
    }

    override fun setLanguage(lang: String) {
        TODO("Not yet implemented")
    }

    override fun setTemp(temp: String) {
        TODO("Not yet implemented")
    }


    override fun setUnit(unit: String) {
        TODO("Not yet implemented")
    }

    override fun setSpeed(speed: String) {
        TODO("Not yet implemented")
    }

    override fun setLocation(location: String) {
        TODO("Not yet implemented")
    }

    override fun setLongitude(lng: Double) {
        TODO("Not yet implemented")
    }

    override fun setLatitude(lat: Double) {
        TODO("Not yet implemented")
    }

    override fun getTemp(): String {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): String {
        TODO("Not yet implemented")
    }

    override fun getUnit(): String {
        TODO("Not yet implemented")
    }

    override fun getSpeed(): String {
        TODO("Not yet implemented")
    }

    override fun getLocation(): String {
        TODO("Not yet implemented")
    }

    override fun getLongitude(): Double {
        TODO("Not yet implemented")
    }

    override fun getLatitude(): Double {
        TODO("Not yet implemented")
    }

}