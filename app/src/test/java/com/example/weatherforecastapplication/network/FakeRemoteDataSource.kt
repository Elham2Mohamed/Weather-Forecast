package com.example.weatherforecastapplication.network

import com.example.weatherforecastapplication.model.WeatherData
import kotlinx.coroutines.flow.Flow


class FakeRemoteDataSource:RemoteDataSource{
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        apiKey: String
    ): Flow<WeatherData> {
        TODO("Not yet implemented")
    }

}