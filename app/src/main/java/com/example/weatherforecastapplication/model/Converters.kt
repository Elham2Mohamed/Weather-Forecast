package com.example.weatherforecastapplication.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromWeatherEntryList(value: List<WeatherEntry>?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWeatherEntryList(value: String?): List<WeatherEntry>? {
        val listType = object : TypeToken<List<WeatherEntry>?>() {}.type
        return Gson().fromJson(value, listType)
    }


    @TypeConverter
    fun fromCity(value: City?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCity(value: String?): City? {
        val type = object : TypeToken<City?>() {}.type
        return Gson().fromJson(value, type)
    }
}
