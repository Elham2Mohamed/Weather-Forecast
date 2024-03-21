package com.example.weatherforecastapplication.model

import com.example.weatherforecastapplication.db.IWeatherLocalDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImp(private val remoteDataSource: RemoteDataSourceImpl,
                           private var localDataSource: IWeatherLocalDataSource
):WeatherRepository {

    companion object{
        private var instance:WeatherRepositoryImp?=null
        fun getInstance(remoteDataSourceImpl: RemoteDataSourceImpl,
                        localDataSource: IWeatherLocalDataSource):WeatherRepositoryImp {

            return instance?: synchronized(this){
                val temp =WeatherRepositoryImp(remoteDataSourceImpl,localDataSource)
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

    override suspend fun getFAVWeathers(): Flow<List<WeatherData>> {
        return localDataSource.getAllWeathers()
    }

    override suspend fun insertWeather(weatherData: WeatherData) {
        return localDataSource.insertWeather(weatherData)
    }

    override suspend fun deleteWeather(weatherData: WeatherData) {
       return localDataSource.deleteWeather(weatherData)
    }
    override suspend fun getWeatherById(id: Long): WeatherData? {
        return localDataSource.getWeatherById(id)
    }
}
