package com.example.weatherforecastapplication.model

import com.example.weatherforecastapplication.db.IWeatherLocalDataSource
import com.example.weatherforecastapplication.db.SettingsLocalDataSource
import com.example.weatherforecastapplication.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImp(private val remoteDataSource: RemoteDataSource,
                           private var localDataSource: IWeatherLocalDataSource,
                           private var settingsLocalDataSource: SettingsLocalDataSource
):WeatherRepository {

    companion object{
        private var instance:WeatherRepositoryImp?=null
        fun getInstance(remoteDataSourceImpl: RemoteDataSource,
                        localDataSource: IWeatherLocalDataSource,
                        settingsLocalDataSource: SettingsLocalDataSource):WeatherRepositoryImp {

            return instance?: synchronized(this){
                val temp =WeatherRepositoryImp(remoteDataSourceImpl,localDataSource,settingsLocalDataSource)
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

    override fun setLanguage(lang: String) {
        settingsLocalDataSource.setLanguage(lang)
    }

    override fun setTemp(temp: String) {
       settingsLocalDataSource.setTemp(temp)
    }

    override fun setUnit(unit: String) {
        settingsLocalDataSource.setUnit(unit)
    }

    override fun setSpeed(speed: String) {
        settingsLocalDataSource.setSpeed(speed)
    }

    override fun setLocation(location: String) {
        settingsLocalDataSource.setLocation(location)
    }

    override fun setLongitude(lng: Double) {
        settingsLocalDataSource.setLongitude(lng)
    }

    override fun setLatitude(lat: Double) {
        settingsLocalDataSource.setLatitude(lat)
    }

    override fun getTemp():String {
        return settingsLocalDataSource.getTemp()
    }

    override fun getLanguage(): String {
        return settingsLocalDataSource.getLanguage()
    }

    override fun getUnit(): String {
        return settingsLocalDataSource.getUnit()
    }

    override fun getSpeed(): String {
        return settingsLocalDataSource.getSpeed()
    }

    override fun getLocation(): String {
        return settingsLocalDataSource.getLocation()
    }

    override fun getLongitude(): Double {
        return settingsLocalDataSource.getLongitude()
    }

    override fun getLatitude(): Double {
        return settingsLocalDataSource.getLatitude()
    }


}
