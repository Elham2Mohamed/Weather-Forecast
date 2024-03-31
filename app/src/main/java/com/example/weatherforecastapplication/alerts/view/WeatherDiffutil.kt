package com.example.weatherforecastapplication.alerts.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecastapplication.model.AlertWeather
class WeatherDiffutil : DiffUtil.ItemCallback<AlertWeather>() {
    override fun areItemsTheSame(oldItem: AlertWeather, newItem: AlertWeather): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AlertWeather, newItem: AlertWeather): Boolean {
        return oldItem == newItem
    }
}