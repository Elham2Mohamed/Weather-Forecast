package com.example.weatherforecastapplication.db

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
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
class WeatherDAOTest {


    lateinit var database: AppDataBase
    lateinit var dao: WeatherDAO

    @get:Rule
    val rule= InstantTaskExecutorRule()

    @Before
    fun setUp(){
        database= Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        ).build()
        dao=database.getProductDAO()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun saveWeatherAndGetWeatherById_WeatherWithId25_theSameWeather()= runTest {

        val weather1=WeatherData(25," ",0,0, emptyList(),null)

        dao.insertWeather(weather1)

        val result=dao.getWeatherById(weather1.id)

        MatcherAssert.assertThat(result, CoreMatchers.not(CoreMatchers.nullValue()))
        MatcherAssert.assertThat(result, CoreMatchers.`is`(weather1))

    }

    @Test
    fun deleteWeatherAndGetWeatherById_WeatherWithId25_WeatherEqualNull()= runTest {
        val weather1=WeatherData(25," ",0,0, emptyList(),null)

        dao.insertWeather(weather1)

        dao.deleteWeather(weather1)

        val result=dao.getWeatherById(weather1.id)

        assertNull(result)
    }





    @ExperimentalCoroutinesApi
    @Test
    fun getAllWeathers_returnListOfWeather() = runBlockingTest {

        val weather2 = WeatherData(1, " ", 0, 0, emptyList(), null)
        val weather3 = WeatherData(2, " ", 0, 0, emptyList(), null)

        val weathers = listOf(weather2, weather3)

        dao.insertWeather(weather2)
        dao.insertWeather(weather3)

        var result = listOf<WeatherData>()
        dao.getAllWeathers().take(1).collect {
            result = it
        }

        assertEquals(weathers, result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllWeathers_emptyListOfWeather() = runBlockingTest {

        var result = listOf<WeatherData>()
        dao.getAllWeathers().take(1).collect {
            result = it
        }
        val test = emptyList<WeatherData>()
        assertEquals(test, result)
    }

}