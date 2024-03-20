package com.example.weatherforecastapplication.favorite.view

import com.example.weatherforecastapplication.model.WeatherData

interface FavOnClickListener {
    fun deleteItem(weatherData: WeatherData)
    fun onClickItem(weatherData: WeatherData)
}