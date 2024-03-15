package com.example.weatherforecastapplication.network

import com.example.weatherforecastapplication.model.WeatherData

sealed class ApiState {
    class Success(val data:WeatherData):ApiState()
    class Failure(val msg:Throwable):ApiState()
    object Loading:ApiState()

}
