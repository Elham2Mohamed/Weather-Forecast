package com.example.weatherforecastapplication.model

import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImp(private val remoteDataSource: RemoteDataSourceImpl):WeatherRepository {

    companion object{
        private var instance:WeatherRepositoryImp?=null
        fun getInstance(remoteDataSourceImpl: RemoteDataSourceImpl):WeatherRepositoryImp {

            return instance?: synchronized(this){
                val temp =WeatherRepositoryImp(remoteDataSourceImpl)
                instance=temp
                temp
            }

        }
    }
    override suspend fun getCurrentWeather(lat: Double, lon: Double,lang:String,units :String, apiKey: String): Flow<WeatherData> {
        return remoteDataSource.getCurrentWeather(lat, lon,lang,units, apiKey)
    }
    override suspend fun getCurrentWeather(lat: Double, lon: Double,lang:String, apiKey: String): Flow<WeatherData> {
        return remoteDataSource.getCurrentWeather(lat, lon,lang, apiKey)
    }
}
