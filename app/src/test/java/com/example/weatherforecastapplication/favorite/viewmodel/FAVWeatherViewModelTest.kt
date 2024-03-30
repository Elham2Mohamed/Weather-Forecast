package com.example.weatherforecastapplication.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mymvvmapplication.favproduct.viewmodel.FAVWeatherViewModel
import com.example.testing_day1.data.source.FakeRepository
import com.example.weatherforecastapplication.MainCoroutinRule
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherData
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FAVWeatherViewModelTest {

    @get:Rule
    val myRule= InstantTaskExecutorRule()

    @get:Rule
    val myMainCoroutinRule= MainCoroutinRule()

    val weather1=WeatherData(0," ",0,0, emptyList(),null)
    val weather2=WeatherData(1," ",0,0, emptyList(),null)

    val weather3=WeatherData(2," ",0,0, emptyList(),null)
    val weather4=WeatherData(3," ",0,0, emptyList(),null)

    val weathers= listOf<WeatherData>(weather1,weather2,weather3)


    lateinit var viewModel:FAVWeatherViewModel
    lateinit var  repo:FakeRepository
    @Before
    fun setUp(){
        repo = FakeRepository(RemoteDataSourceImpl(),mutableListOf(),emptyList<WeatherData>(),mutableListOf())
        viewModel = FAVWeatherViewModel(repo)
    }

    @Test
    fun deleteWeather_newWeatherRemoved() {
        // Given create object of ViewModel
        // When call deleteWeather()
        runBlocking {
            viewModel.insertWeather(weather1)
            viewModel.deleteWeather(weather1)
        }

        // Then assert if new weather is removed
        MatcherAssert.assertThat(viewModel.weathres.value, CoreMatchers.nullValue())
    }

    @Test
    fun insertWeather_newWeatherUpdated() {
        // Given create object of ViewModel
        // When call insertWeather()
        runBlocking {
            viewModel.insertWeather(weather1)
        }

        // Then assert if new weather is in the list
        runBlocking {
            val result = viewModel.getWeatherById(0)
            MatcherAssert.assertThat(result, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(result, CoreMatchers.equalTo(weather1))
        }
    }

    @Test
    fun getLocalWeathers_returnWeathersEqualNull() {
        // Given create object of ViewModel and insert multiple weather data
        // When retrieve the list of weather =null
        val result = runBlocking {
            viewModel.weathres.value
        }

        // Then assert if the retrieved list matches the expected list
        MatcherAssert.assertThat(result, CoreMatchers.nullValue())
    }



}