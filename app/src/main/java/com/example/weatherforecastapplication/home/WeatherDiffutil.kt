package com.example.weatherforecastapplication.home

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherEntry

class WeatherDiffutil : DiffUtil.ItemCallback<WeatherEntry>() {
    override fun areItemsTheSame(oldItem: WeatherEntry, newItem: WeatherEntry): Boolean {
        return oldItem.dt_txt == newItem.dt_txt
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: WeatherEntry, newItem: WeatherEntry): Boolean {
        return newItem == oldItem
    }
}