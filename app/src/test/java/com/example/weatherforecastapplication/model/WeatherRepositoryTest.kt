package com.example.weatherforecastapplication.model

import com.example.weatherforecastapplication.db.FakeAlertWeatherLocalDataSource
import com.example.weatherforecastapplication.db.FakeSettingsLocalDataSource
import com.example.weatherforecastapplication.db.FakeWeatherLocalDataSource
import com.example.weatherforecastapplication.network.FakeRemoteDataSource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotSame
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
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

    val weathers= listOf<WeatherData>()
    val alerts= listOf<AlertWeather>()
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
     fun insertWeather1_andCheckIsSame()= runBlockingTest {
        repository.insertWeather(weather1)
        val result=repository.getWeatherById(0)
        assertEquals(weather1 ,result)

    }
    @Test
    fun deleteWeather12_andCheckListIsEmpty() = runBlockingTest {
        repository.insertWeather(weather1)
        repository.deleteWeather(weather1)
        val result = repository.getWeatherById(0)
        assertNull(result)
    }

    @Test
    fun getWeatherByIdEqualZero()=runBlockingTest{
        repository.insertWeather(weather1)
        val result=repository.getWeatherById(0)
        assertEquals(weather1 ,result)
    }


    @Test
    fun insertAlertWeather1_andCheckIsSame()= runBlockingTest {
        repository.insertAlertWeather(alert1)
        val result=repository.getAlertWeatherById(0)
        assertEquals(alert1 ,result)

    }
    @Test
    fun deleteAlertWeather1_andCheckListIsEmpty()=runBlockingTest{
        repository.insertAlertWeather(alert1)
        repository.deleteAlertWeather(alert1)
        val result = repository.getAlertWeatherById(0)
        assertNull(result)

    }
    @Test
    fun getAlertWeatherByIdEqualZero()=runBlockingTest{
        repository.insertAlertWeather(alert1)
        val result=repository.getAlertWeatherById(0)
        assertEquals(alert1 ,result)
    }


    @ExperimentalCoroutinesApi
    @Test
    fun getAllWeathers_emptyListOfWeather() = runBlockingTest {

        var result = listOf<WeatherData>()
        repository.getFAVWeathers().take(1).collect {
            result = it
        }
        val test = emptyList<WeatherData>()
        assertEquals(test, result)
    }
    @ExperimentalCoroutinesApi
    @Test
    fun getAllWeathers_returnListOfWeather() = runBlockingTest {

        val weather2 = WeatherData(1, " ", 0, 0, emptyList(), null)
        val weather3 = WeatherData(2, " ", 0, 0, emptyList(), null)

        val weathers = listOf(weather2, weather3)

        repository.insertWeather(weather2)
        repository.insertWeather(weather3)

        var result = listOf<WeatherData>()
        repository.getFAVWeathers().take(1).collect {
            result = it
        }

        assertEquals(weathers, result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllAlertsWeathers_emptyListOfWeather() = runBlockingTest {

        var result = listOf<AlertWeather>()
        repository.getAlertWeathers()?.take(1)?.collect {
            result = it
        }
        val test = emptyList<AlertWeather>()
        assertEquals(test, result)
    }
    @ExperimentalCoroutinesApi
    @Test
    fun getAllAlertsWeathers_returnListOfWeather() = runBlockingTest {

        val alert2=AlertWeather(1,"","",0.0,0.0,"")
        val alert3=AlertWeather(2,"","",0.0,0.0,"")

        val alerts = listOf(alert2, alert3)

        repository.insertAlertWeather(alert2)
        repository.insertAlertWeather(alert3)

        var result = listOf<AlertWeather>()
        repository.getAlertWeathers()?.take(1)?.collect {
            result = it
        }

        assertEquals(alerts, result)
    }


}