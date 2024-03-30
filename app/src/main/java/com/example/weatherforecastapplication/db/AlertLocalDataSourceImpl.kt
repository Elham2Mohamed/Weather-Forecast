package com.example.weatherforecastapplication.db

import android.content.Context
import com.example.weatherforecastapplication.model.AlertWeather
import kotlinx.coroutines.flow.Flow

class AlertLocalDataSourceImpl(context: Context):AlertLocalDataSource {
    private val dao:AlertWeatherDAO by lazy {
        val db:AlertDataBase=AlertDataBase.getInstance(context)
        db.getAlertWeatherDAO()
    }
    override suspend fun insertAlertWeather(alertWeather: AlertWeather) {
        dao.insertAlertWeather(alertWeather)
    }

    override suspend fun deleteAlertWeather(alertWeather: AlertWeather) {
       dao.deleteAlertWeather(alertWeather)
    }

    override suspend fun getAllAlertsWeathers(): Flow<List<AlertWeather>> {
        return dao.getAllAlertsWeathers()
    }

    override suspend fun getAlertWeatherById(id: Long): AlertWeather? {
        return  dao.getAlertWeatherById(id)
    }
}