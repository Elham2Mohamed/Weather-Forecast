package com.example.weatherforecastapplication.home.viewModle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mymvvmapplication.favproduct.viewmodel.FAVWeatherViewModel
import com.example.testing_day1.data.source.FakeRepository
import com.example.weatherforecastapplication.MainCoroutinRule
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherData
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doReturn


@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    @get:Rule
    val myRule = InstantTaskExecutorRule()

    @get:Rule
    val myMainCoroutinRule = MainCoroutinRule()

    lateinit var viewModel: HomeViewModel
    lateinit var repo: FakeRepository
    private lateinit var remoteDataSource: RemoteDataSourceImpl

    @Before
    fun setUp() {
        remoteDataSource=RemoteDataSourceImpl()
        repo = FakeRepository(remoteDataSource, mutableListOf(), emptyList<WeatherData>(),mutableListOf())
        viewModel = HomeViewModel(repo)
    }

    @Test
    fun `getCurrentWeather without units`() {

        val lat = 31.0
        val lon = 35.78
        val lang = "en"
        val apiKey = "39b555dfd3fff6499b8c963689da8e9c"

        viewModel.getCurrentWeather(lat, lon, lang, apiKey)
        assertNotNull(viewModel.currentWeather.value)

    }
}