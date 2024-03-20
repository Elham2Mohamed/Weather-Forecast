package com.example.weatherforecastapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecastapplication.model.Converters
import com.example.weatherforecastapplication.model.WeatherData

@Database(entities= arrayOf(WeatherData::class), version = 1,exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getProductDAO():WeatherDAO

    companion object {
        @Volatile
        private var INSTANCE:AppDataBase?=null

        fun getInstance(context: Context?):AppDataBase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(context!!.applicationContext ,AppDataBase::class.java,"WeatherF").build()
                INSTANCE=instance
                instance
            }
        }
    }
}
