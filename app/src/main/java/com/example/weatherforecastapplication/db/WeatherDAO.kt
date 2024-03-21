package com.example.weatherforecastapplication.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecastapplication.model.WeatherData
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {
    @Query("SELECT * FROM weather_t")
     fun getAllWeathers(): Flow<List<WeatherData>>
    @Query("SELECT * FROM weather_t WHERE id = :id")
    suspend fun getWeatherById(id: Long): WeatherData?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeather(weatherData: WeatherData):Long

    @Delete
    suspend fun deleteWeather(weatherData: WeatherData):Int
}