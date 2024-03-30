package com.example.weatherforecastapplication.alerts.view

import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.WeatherData

interface AlertOnClickListener {
    fun deleteItem(alertWeather: AlertWeather)

}