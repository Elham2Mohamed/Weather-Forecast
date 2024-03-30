package com.example.weatherforecastapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.Converters

@Database(entities= arrayOf(AlertWeather::class), version = 2,exportSchema = false)
@TypeConverters(Converters::class)
abstract class AlertDataBase: RoomDatabase() {
    abstract fun getAlertWeatherDAO():AlertWeatherDAO

    companion object {
        @Volatile
        private var INSTANCE:AlertDataBase?=null

        fun getInstance(context: Context?):AlertDataBase{
            return INSTANCE?: synchronized(this){val instance = Room.databaseBuilder(
                context!!.applicationContext,
                AlertDataBase::class.java,
                "alert weather"
            ).fallbackToDestructiveMigration().build()
                INSTANCE=instance
                instance
            }
        }
    }
}

