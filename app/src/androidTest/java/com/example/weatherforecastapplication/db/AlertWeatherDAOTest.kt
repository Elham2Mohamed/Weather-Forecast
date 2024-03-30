package com.example.weatherforecastapplication.db

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.WeatherData
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runBlockingTest

import kotlinx.coroutines.test.runBlockingTest

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AlertWeatherDAOTest {


    lateinit var database: AlertDataBase
    lateinit var dao: AlertWeatherDAO

    @get:Rule
    val rule= InstantTaskExecutorRule()

    @Before
    fun setUp(){
        database= Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AlertDataBase::class.java
        ).build()
        dao=database.getAlertWeatherDAO()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun saveWeatherAndGetWeatherById_WeatherWithId25_theSameWeather()= runTest {

        val weather1=AlertWeather(25," ","0",0.0, 0.0,"")

        dao.insertAlertWeather(weather1)

        val result=dao.getAlertWeatherById(weather1.id.toLong())

        MatcherAssert.assertThat(result, CoreMatchers.not(CoreMatchers.nullValue()))
        MatcherAssert.assertThat(result, CoreMatchers.`is`(weather1))

    }

    @Test
    fun deleteWeatherAndGetWeatherById_WeatherWithId25_WeatherEqualNull()= runTest {
        val weather1=AlertWeather(25," ","0",0.0, 0.0,"")
        dao.insertAlertWeather(weather1)

        dao.deleteAlertWeather(weather1)

        val result=dao.getAlertWeatherById(weather1.id.toLong())

        assertNull(result)
    }




    @ExperimentalCoroutinesApi
    @Test
    fun getAllAlertsWeathers_returnListOfWeather() = runBlockingTest {

        val weather1=AlertWeather(1," ","0",0.0, 0.0,"")
        val weather2=AlertWeather(2," ","0",0.0, 0.0,"")

        val weathers = listOf( weather1, weather2)

        dao.insertAlertWeather(weather1)
        dao.insertAlertWeather(weather2)

        var result = listOf<AlertWeather>()
        dao.getAllAlertsWeathers().take(1).collect{
            result=it
        }

        TestCase.assertEquals(weathers, result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllAlertsWeathers_emptyListOfWeather() = runBlockingTest {

        var result = listOf<AlertWeather>()
        dao.getAllAlertsWeathers().take(1).collect{
            result=it
        }

        val test= emptyList<AlertWeather>()
        TestCase.assertEquals( test,result)
    }

}