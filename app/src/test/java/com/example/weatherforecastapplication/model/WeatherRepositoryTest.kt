package com.example.weatherforecastapplication.model

import com.example.weatherforecastapplication.db.FakeAlertWeatherLocalDataSource
import com.example.weatherforecastapplication.db.FakeSettingsLocalDataSource
import com.example.weatherforecastapplication.db.FakeWeatherLocalDataSource
import com.example.weatherforecastapplication.network.FakeRemoteDataSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test


class WeatherRepositoryTest{

    val weather1=WeatherData(0," ",0,0, emptyList(),null)
    val weather2=WeatherData(1," ",0,0, emptyList(),null)

    val weather3=WeatherData(2," ",0,0, emptyList(),null)
    val weather4=WeatherData(3," ",0,0, emptyList(),null)

    val alert1=AlertWeather(0,"","",0.0,0.0,"")
    val alert2=AlertWeather(1,"","",0.0,0.0,"")
    val alert3=AlertWeather(2,"","",0.0,0.0,"")

    val weathers= listOf<WeatherData>(weather1,weather2,weather3)
    val alerts= listOf<AlertWeather>(alert1,alert2,alert3)
    lateinit var fakeWeatherLocalDataSource: FakeWeatherLocalDataSource
    lateinit var fakeAlertWeatherLocalDataSource: FakeAlertWeatherLocalDataSource
    lateinit var fakeSettingsLocalDataSource: FakeSettingsLocalDataSource
    lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    lateinit var repository: WeatherRepositoryImp

    @Before
    fun setUp(){
       fakeWeatherLocalDataSource=FakeWeatherLocalDataSource(weathers.toMutableList())
        fakeAlertWeatherLocalDataSource=FakeAlertWeatherLocalDataSource(alerts.toMutableList())
        fakeRemoteDataSource= FakeRemoteDataSource()
        fakeSettingsLocalDataSource= FakeSettingsLocalDataSource()
        repository= WeatherRepositoryImp(fakeRemoteDataSource,fakeWeatherLocalDataSource, fakeSettingsLocalDataSource,fakeAlertWeatherLocalDataSource)
    }

    @Test
     fun insertWeather1_inIndex3()= runBlockingTest {
        repository.insertWeather(weather1)
        val result=repository.getWeatherById(3)
        assertEquals(weather1 ,result)

    }
    @Test
     fun deleteWeather12_andCheckListHasWeather3_Only()=runBlockingTest{
        repository.deleteWeather(weather1)
        repository.deleteWeather(weather2)
        val result=repository.getWeatherById(0)
        assertEquals(weather3 ,result)

    }
    @Test
    fun getWeatherByIdEqualZero()=runBlockingTest{
        val result=repository.getWeatherById(0)
        assertEquals(weather1 ,result)
    }


    @Test
    fun insertAlertWeather1_inIndex3()= runBlockingTest {
        repository.insertAlertWeather(alert1)
        val result=repository.getAlertWeatherById(3)
        assertEquals(weather1 ,result)

    }
    @Test
    fun deleteAlertWeather12_andCheckListHasWeather3_Only()=runBlockingTest{
        repository.deleteAlertWeather(alert1)
        repository.deleteAlertWeather(alert2)
        val result=repository.getAlertWeatherById(0)
        assertEquals(weather3 ,result)

    }
    @Test
    fun getAlertWeatherByIdEqualZero()=runBlockingTest{
        val result=repository.getAlertWeatherById(0)
        assertEquals(alert1 ,result)
    }

}