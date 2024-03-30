package com.example.weatherforecastapplication.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.WeatherData
import junit.framework.TestCase
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
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

class AlertDataBaseTest{

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: AlertLocalDataSourceImpl
    private lateinit var database: AlertDataBase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AlertDataBase::class.java
        ).allowMainThreadQueries()
            .build()
        localDataSource = AlertLocalDataSourceImpl(ApplicationProvider.getApplicationContext())

    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertWeather_retrievesWeather() = runTest {
        val weather1= AlertWeather(25," ","0",0.0, 0.0,"")

        localDataSource.insertAlertWeather(weather1)

        val result = localDataSource.getAlertWeatherById(weather1.id.toLong())

        assertThat(result, `is`(weather1))
    }

    @Test
    fun deleteWeather_retrievedWeather() = runTest {
        val weather1=AlertWeather(25," ","0",0.0, 0.0,"")

        localDataSource.insertAlertWeather(weather1)

        localDataSource.deleteAlertWeather(weather1)

        val result = localDataSource.getAlertWeatherById(weather1.id.toLong())

        assertNull(result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllAlertsWeathers_returnListOfWeather() = runBlockingTest {

        val weather1=AlertWeather(1," ","0",0.0, 0.0,"")
        val weather2=AlertWeather(2," ","0",0.0, 0.0,"")

        val weathers = listOf( weather1, weather2)

        localDataSource.insertAlertWeather(weather1)
        localDataSource.insertAlertWeather(weather2)

        var result = listOf<AlertWeather>()
        localDataSource.getAllAlertsWeathers().take(1).collect{
            result=it
        }

        TestCase.assertEquals(weathers, result)
    }

//    @ExperimentalCoroutinesApi
//    @Test
//    fun getAllAlertsWeathers_emptyListOfWeather() = runBlockingTest {
//        var result = listOf<AlertWeather>()
//        localDataSource.getAllAlertsWeathers().take(1).collect{
//            result=it
//        }
//
//        val test= emptyList<AlertWeather>()
//        TestCase.assertEquals( test,result)
//    }
}
