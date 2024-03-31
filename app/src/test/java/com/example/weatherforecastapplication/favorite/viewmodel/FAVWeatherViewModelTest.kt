package com.example.weatherforecastapplication.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mymvvmapplication.favproduct.viewmodel.FAVWeatherViewModel
import com.example.testing_day1.data.source.FakeRepository
import com.example.weatherforecastapplication.MainCoroutinRule
import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherData
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
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

    val weathers= listOf<WeatherData>()


    lateinit var viewModel:FAVWeatherViewModel
    lateinit var  repo:FakeRepository
    @Before
    fun setUp(){
        repo = FakeRepository(RemoteDataSourceImpl(),mutableListOf(),emptyList<WeatherData>(),mutableListOf())
        viewModel = FAVWeatherViewModel(repo)
    }

    @Test
     fun deleteWeather_newWeatherRemoved() {

        runBlocking {
            viewModel.insertWeather(weather1)
        }
        runBlocking {
            viewModel.deleteWeather(weather1)
        }
        val result =runBlocking {
            viewModel.getWeatherById(0)
        }
        assertNull(result)

    }

    @Test
    fun insertWeather_newWeatherUpdated() {

        runBlocking {
            viewModel.insertWeather(weather1)
        }


        runBlocking {
            val result = viewModel.getWeatherById(0)
            MatcherAssert.assertThat(result, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(result, CoreMatchers.equalTo(weather1))
        }
    }
    @ExperimentalCoroutinesApi
    @Test
    fun getAllWeathers_emptyListOfWeather() = runBlockingTest {

        var result =viewModel.weathres.value?.toList()
        val test = emptyList<WeatherData>()
        TestCase.assertEquals(test, result)
    }
    @ExperimentalCoroutinesApi
    @Test
    fun getAllWeathers_returnListOfWeather()  {
        runBlocking {
            viewModel.insertWeather(weather2)
            viewModel.insertWeather(weather3)
        }

        val result =  runBlocking { viewModel.weathres.value}


        assertEquals(listOf(weather2, weather3), result)
    }

    @Test
    fun getWeatherByIdEqualZero()=runBlockingTest{
        runBlocking {
            viewModel.insertWeather(weather1)
        }
        val result=runBlocking {viewModel.getWeatherById(0)}
        assertEquals(weather1, result)
    }

}