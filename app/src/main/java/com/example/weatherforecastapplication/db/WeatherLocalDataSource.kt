package com.example.weatherforecastapplication.db

import android.content.Context
import com.example.weatherforecastapplication.model.WeatherData
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(context: Context) :IWeatherLocalDataSource{

    private val dao:WeatherDAO by lazy {
        val db:AppDataBase=AppDataBase.getInstance(context)
        db.getProductDAO()
    }
    override suspend fun insertWeather(weatherData: WeatherData) {
        dao.insertWeather(weatherData)
    }

    override suspend fun deleteWeather(weatherData: WeatherData) {
        dao.deleteWeather(weatherData)
    }

    override suspend fun getAllWeathers(): Flow<List<WeatherData>> {
        return dao.getAllWeathers()
    }
}