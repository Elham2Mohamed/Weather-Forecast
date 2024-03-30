package com.example.weatherforecastapplication.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecastapplication.model.AlertWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertWeatherDAO {
    @Query("SELECT * FROM alert")
    fun getAllAlertsWeathers(): Flow<List<AlertWeather>>
    @Query("SELECT * FROM alert WHERE id = :id")
    suspend fun getAlertWeatherById(id: Long): AlertWeather?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlertWeather(alertWeather: AlertWeather):Long

    @Delete
    suspend fun deleteAlertWeather(alertWeather: AlertWeather):Int

}
