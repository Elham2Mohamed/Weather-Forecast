package com.example.weatherforecastapplication.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.databinding.FragmentHomeBinding
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherEntry
import com.example.weatherforecastapplication.model.WeatherRepositoryImp
import com.example.weatherforecastapplication.network.ApiState
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var loader_view: ProgressBar
    private lateinit var tvcity:TextView
    private lateinit var tvDate:TextView
    private lateinit var tvDegree:TextView
    private lateinit var tvDesc:TextView
    private lateinit var tvPressure:TextView
    private lateinit var tvWind:TextView
    private lateinit var tvHum:TextView
    private lateinit var tvCloud:TextView
    private lateinit var tvTemp:TextView
    private lateinit var tvVisibility:TextView
    private lateinit var weatherData:WeatherData
    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var recyclerView1: RecyclerView
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var layoutManager1: LinearLayoutManager
    private lateinit var recyclerView2: RecyclerView
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var layoutManager2: LinearLayoutManager

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader_view = view.findViewById(R.id.loader)
        tvcity = view.findViewById(R.id.tvCity)
        tvDate = view.findViewById(R.id.tvDate)
        tvDegree = view.findViewById(R.id.tvDegre)
        tvDesc = view.findViewById(R.id.tvDesc)
        tvPressure = view.findViewById(R.id.tvPressrue)
        tvWind = view.findViewById(R.id.tvWin)
        tvHum = view.findViewById(R.id.tvHum)
        tvCloud = view.findViewById(R.id.tvCloud)
        tvTemp = view.findViewById(R.id.tvTemp)
        tvVisibility = view.findViewById(R.id.tvVisibility)
        recyclerView1 = view.findViewById(R.id.recyclerView1)
        recyclerView2 = view.findViewById(R.id.recyclerView2)

        val repository = WeatherRepositoryImp.getInstance(RemoteDataSourceImpl())
        val viewModelFactory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        hourlyAdapter = HourlyAdapter {
            Log.i("TAG", "onViewCreated: ")
        }
        layoutManager1 = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView1.layoutManager = layoutManager1
        recyclerView1.adapter = hourlyAdapter

        dailyAdapter = DailyAdapter {
            Log.i("TAG", "onViewCreated: ")
        }
        layoutManager2 = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView2.layoutManager = layoutManager2
        recyclerView2.adapter = dailyAdapter

        lifecycleScope.launch {
            viewModel.currentWeather.collect { result ->
                when (result) {
                    is ApiState.Loading -> {
                        loader_view.visibility = View.VISIBLE
                        recyclerView1.visibility = View.GONE
                        recyclerView2.visibility = View.GONE
                    }
                    is ApiState.Success -> {
                        loader_view.visibility = View.GONE
                        recyclerView1.visibility = View.VISIBLE
                        recyclerView2.visibility = View.VISIBLE

                        val data = result.data.list.subList(0, 8)
                        hourlyAdapter.submitList(data)

                        val filteredList = mutableListOf<WeatherEntry>()
                        var startIndex = 0
                        while (startIndex < result.data.list.size) {
                            val endIndex = minOf(startIndex + 1, result.data.list.size)
                            filteredList.addAll(result.data.list.subList(startIndex, endIndex))
                            startIndex += 8
                        }
                        dailyAdapter.submitList(filteredList)

                        tvcity.text = result.data.city?.name ?: "cairo"
                        tvDate.text = result.data.list[0].dt_txt
                        tvDegree.text = result.data.list[0].wind.deg.toString() + "°C"
                        tvDesc.text = result.data.list[0].weather[0].description
                        tvPressure.text = result.data.list[0].main.pressure.toString()
                        tvWind.text = result.data.list[0].wind.speed.toString()
                        tvHum.text = result.data.list[0].main.humidity.toString()
                        tvCloud.text = result.data.list[0].clouds.all.toString()
                        tvTemp.text = result.data.list[0].main.temp.toString()
                        tvVisibility.text = result.data.list[0].visibility.toString()
                    }
                    is ApiState.Failure -> {
                        loader_view.visibility = View.GONE
                        recyclerView1.visibility = View.GONE
                        recyclerView2.visibility = View.GONE
                        Log.i("TAG", "onViewCreated: Error")
                    }
                }
            }
        }

        viewModel.getCurrentWeather(44.34, 10.99, "7827a54d5a63cea51b327c7a48bf36ec")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}