package com.example.weatherforecastapplication.db

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.WeatherData
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class AppDataBaseTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: WeatherLocalDataSource
    private lateinit var database: AppDataBase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        ).allowMainThreadQueries()
            .build()
        localDataSource = WeatherLocalDataSource(ApplicationProvider.getApplicationContext())

    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertWeather_retrievesWeather() = runTest {
        val weather1 = WeatherData(25, " ", 0, 0, emptyList(), null)

        localDataSource.insertWeather(weather1)

        val result = localDataSource.getWeatherById(weather1.id)

        assertThat(result, `is`(weather1))
    }

    @Test
    fun deleteWeather_retrievedWeather() = runTest {
        val weather1 = WeatherData(25, " ", 0, 0, emptyList(), null)

        localDataSource.insertWeather(weather1)

        localDataSource.deleteWeather(weather1)

        val result = localDataSource.getWeatherById(weather1.id)

        assertNull(result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllWeathers_returnListOfWeather() = runBlockingTest {

        val weather2 = WeatherData(1, " ", 0, 0, emptyList(), null)
        val weather3 = WeatherData(2, " ", 0, 0, emptyList(), null)

        val weathers = listOf(weather2, weather3)

        localDataSource.insertWeather(weather2)
        localDataSource.insertWeather(weather3)

        var result = listOf<WeatherData>()
        localDataSource.getAllWeathers().take(1).collect {
            result = it
        }

        assertEquals(weathers, result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllWeathers_emptyListOfWeather() = runBlockingTest {

        var result = listOf<WeatherData>()
        localDataSource.getAllWeathers().take(1).collect {
            result = it
        }
        val test = emptyList<WeatherData>()
        assertEquals(test, result)
    }

}
