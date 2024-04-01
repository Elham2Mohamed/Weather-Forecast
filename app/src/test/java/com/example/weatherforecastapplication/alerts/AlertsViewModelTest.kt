package com.example.weatherforecastapplication.alerts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.testing_day1.data.source.FakeRepository
import com.example.weatherforecastapplication.MainCoroutinRule
import com.example.weatherforecastapplication.alerts.viewModel.AlertsViewModel
import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherData
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertsViewModelTest {


//    @get:Rule
//    val myRule = InstantTaskExecutorRule()

    @get:Rule
    val myMainCoroutinRule = MainCoroutinRule()

    val alert1 = AlertWeather(0, "", "", 0.0, 0.0, "")
    val alert2 = AlertWeather(1, "", "", 0.0, 0.0, "")
    val alert3 = AlertWeather(2, "", "", 0.0, 0.0, "")

    val alerts = listOf<AlertWeather>()

    lateinit var viewModel: AlertsViewModel
    lateinit var repo: FakeRepository

    @Before
    fun setUp() {
        repo = FakeRepository(
            RemoteDataSourceImpl(),
            mutableListOf(),
            emptyList<WeatherData>(),
            mutableListOf()
        )
        viewModel = AlertsViewModel(repo)
    }

    @Test
    fun deleteWeather_newWeatherRemoved() {
        runBlocking {
            viewModel.insertAlertWeather(alert1)
        }
        runBlocking {
            viewModel.deleteAlertWeather(alert1)
        }
        val result = runBlocking { viewModel.getAlertWeatherById(0) }
        TestCase.assertNull(result)

    }

    @Test
    fun insertWeather_newWeatherUpdated() {
        runBlocking {
            viewModel.insertAlertWeather(alert1)
        }
        runBlocking {
            val result = viewModel.getAlertWeatherById(0)
            MatcherAssert.assertThat(result, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(result, CoreMatchers.equalTo(alert1))
        }
    }

    @Test
    fun getLocalWeathers_returnWeathersEqualNull() {
        val result = runBlocking {
            viewModel.alerts.value
        }
        MatcherAssert.assertThat(result, CoreMatchers.nullValue())
    }


    @Test
    fun getWeatherByIdEqualZero() = runBlockingTest {
        runBlocking {
            viewModel.insertAlertWeather(alert1)
        }
        val result = runBlocking { viewModel.getAlertWeatherById(0) }
        TestCase.assertEquals(alert1, result)
    }


}