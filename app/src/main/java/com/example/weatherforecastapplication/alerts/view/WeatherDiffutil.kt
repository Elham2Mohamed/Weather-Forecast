package com.example.weatherforecastapplication.alerts.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecastapplication.model.AlertWeather
class WeatherDiffutil : DiffUtil.ItemCallback<AlertWeather>() {
    override fun areItemsTheSame(oldItem: AlertWeather, newItem: AlertWeather): Boolean {
        return oldItem.id == newItem.id // Change id to the unique identifier of AlertWeather
    }

    override fun areContentsTheSame(oldItem: AlertWeather, newItem: AlertWeather): Boolean {
        return oldItem == newItem
    }
}