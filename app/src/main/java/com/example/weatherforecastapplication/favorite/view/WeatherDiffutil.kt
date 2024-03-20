package com.example.weatherforecastapplication.favorite.view

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecastapplication.model.Weather
import com.example.weatherforecastapplication.model.WeatherData

class WeatherDiffutil : DiffUtil.ItemCallback<WeatherData>() {
    override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
        return oldItem.cod == newItem.cod
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
        return newItem == oldItem
    }
}