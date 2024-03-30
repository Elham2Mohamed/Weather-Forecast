package com.example.weatherforecastapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert")
data class AlertWeather(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val city: String,
    val date:String,
    val lat: Double,
    val lon: Double,
    val type:String
)
