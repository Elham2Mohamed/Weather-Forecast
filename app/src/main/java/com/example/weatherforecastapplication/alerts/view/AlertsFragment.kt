package com.example.weatherforecastapplication.alerts.view

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.util.Calendar
import android.view.ViewGroup
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.impl.WorkManagerImpl
import androidx.work.workDataOf
import com.example.mymvvmapplication.favproduct.viewmodel.AlertWeatherViewModelFactory
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.alerts.Worker
import com.example.weatherforecastapplication.alerts.viewModel.AlertsViewModel
import com.example.weatherforecastapplication.databinding.FragmentAlertsBinding
import com.example.weatherforecastapplication.db.AlertLocalDataSourceImpl
import com.example.weatherforecastapplication.db.SettingsLocalDataSourceImpl
import com.example.weatherforecastapplication.db.WeatherLocalDataSource
import com.example.weatherforecastapplication.favorite.view.FAVWeatherAdapter
import com.example.weatherforecastapplication.home.view.HomeFragment
import com.example.weatherforecastapplication.model.AlertWeather
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherRepositoryImp
import com.example.weatherforecastapplication.settings.view.SettingsFragment
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModel
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.log

class AlertsFragment : Fragment(), AlertOnClickListener {

    lateinit var btnAdd: FloatingActionButton
    lateinit var dialog: Dialog
    lateinit var tvDate: TextView
    private var _binding: FragmentAlertsBinding? = null
    lateinit var factory: AlertWeatherViewModelFactory
    lateinit var viewModel: AlertsViewModel
    lateinit var sFactory: SettingsViewModelFactory
    lateinit var sViewModel: SettingsViewModel
    var date: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlertWeatherAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("LogNotTimber", "FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        factory = AlertWeatherViewModelFactory(
            WeatherRepositoryImp.getInstance(
                RemoteDataSourceImpl(),
                WeatherLocalDataSource(requireContext()),
                SettingsLocalDataSourceImpl(requireContext()),
                AlertLocalDataSourceImpl(requireContext())
            )
        )

        viewModel = ViewModelProvider(this, factory).get(AlertsViewModel::class.java)


        sFactory = SettingsViewModelFactory(
            WeatherRepositoryImp.getInstance(
                RemoteDataSourceImpl(),
                WeatherLocalDataSource(requireContext()),
                SettingsLocalDataSourceImpl(requireContext()),
                AlertLocalDataSourceImpl(requireContext())
            )
        )

        sViewModel =
            ViewModelProvider(requireActivity(), sFactory).get(SettingsViewModel::class.java)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.alert_daialog)
        val rgNotification = dialog.findViewById<RadioGroup>(R.id.rgNotifications)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
        //  btnSave.visibility = View.GONE
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        tvDate = dialog.findViewById<TextView>(R.id.tvDate)
        btnAdd = view.findViewById(R.id.btnAddLoction)

        recyclerView = view.findViewById(R.id.list_item)
        adapter = AlertWeatherAdapter(this)
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter


        var latitude = 0.0
        var longitude = 0.0
        var type = "Alert"
        var id = 0
        viewModel.alerts.observe(viewLifecycleOwner) { weatherList ->

            val filteredList = weatherList
            val updatedList = filteredList + viewModel.newAlertItems
            adapter.submitList(updatedList)
        }

        btnAdd.setOnClickListener {

            val navController = findNavController()
            val action =
                AlertsFragmentDirections.actionNavigationAlertsToNavigationMap()
            action.id = 3
            navController.navigate(action)
        }
        if (arguments != null) {
            latitude = arguments?.getString("lat")!!.toDouble()
            longitude = arguments?.getString("log")!!.toDouble()
            addToCalendar(requireContext())
            val city = getCityName(requireContext(), latitude, longitude)

            rgNotification.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rBtnNotifications -> {
                        runBlocking {
                            showEnableDialog()
                        }
                        if (!Settings.canDrawOverlays(requireContext())) {

                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package: com.example.weatherforecastapplication.alerts.view")
                            )
                            startActivityForResult(intent, 123)
                        }

                        type = "Alert"
                    }

                    R.id.rBtn2Notifications -> {
                        val alert = date?.let {

                            val notificationManager = requireContext().getSystemService(
                                NotificationManager::class.java
                            ) as NotificationManager
                            if (notificationManager.areNotificationsEnabled()) {
                                Log.i("TAG", "onViewCreated: enable ")
                            } else {
                                Log.i("TAG", "onViewCreated: disable")
                                ActivityCompat.requestPermissions(
                                    requireContext() as Activity,
                                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                    1
                                )
                            }

                        }

                        type = "Notifications"

                    }

                }
            }



            btnSave.setOnClickListener {

                Log.i("TAG", "onViewCreated: date=$date")

                val alert = date?.let {
                    AlertWeather(
                        city = city.toString(),
                        date = it,
                        lat = latitude,
                        lon = longitude,
                        type = type
                    )


                }
                alert?.let {
                    viewModel.insertAlertWeather(it)
                    Log.i("TAG", "onViewCreated: alert before send $alert")
                    id = alert.id
                }

                val request = date?.let { getInitialDelayForFutureDate(it) }?.let {
                    Log.i("TAG", "onViewCreated:${it - System.currentTimeMillis()} ")
                    Log.i("TAG", "onViewCreated: iD ==${id} ")

                    OneTimeWorkRequestBuilder<Worker>()
                        .setInputData(
                            workDataOf(
                                "lat" to latitude,
                                "log" to longitude,
                                "type" to type,
                                "lang" to sViewModel.getLanguage(),
                                "access" to sViewModel.getNotificationAccess(),
                                "date" to date,
                                "city" to city,
                                "id" to id
                            )
                        )
                        .setInitialDelay(it - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                        .build()

                }
                request?.let {
                    Log.i("TAG", "onViewCreated: worker")
                    WorkManager.getInstance(requireContext()).enqueue(it)

                    WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(request.id)
                        .observe(viewLifecycleOwner) { workInfo ->
                            when (workInfo.state) {
                                WorkInfo.State.SUCCEEDED -> {

                                    val outputData = workInfo.outputData
                                    val city = outputData.getString("city")
                                    val date = outputData.getString("date")
                                    val lat = outputData.getDouble("lat", 0.0)
                                    val lon = outputData.getDouble("lon", 0.0)
                                    val type = outputData.getString("type")
                                    val id = outputData.getInt("id", 0)
                                    Log.i("TAG", "onViewCreated:  id = $id")
                                    val alertWeather = AlertWeather(
                                        id = id,
                                        city = city.toString(),
                                        date = date.toString(),
                                        lat = lat,
                                        lon = lon,
                                        type = type.toString()
                                    )
                                    Log.i(
                                        "TAG",
                                        "onViewCreated:  before delete alertWeather = $alertWeather"
                                    )

                                    viewModel.alerts.observe(viewLifecycleOwner) { alerts ->
                                        alerts.forEach {
                                            if (it.date == date) {
                                                Log.i("TAG", "onViewCreated: by id alert  = $it")
                                                viewModel.deleteAlertWeather(it)

                                            }
                                        }
                                    }
                                }

                                WorkInfo.State.FAILED -> {

                                    Log.e("TAG", "Work failed")
                                }

                                WorkInfo.State.CANCELLED -> {

                                    Log.e("TAG", "Work cancelled")
                                }

                                else -> {}
                            }
                        }
                }


                dialog.dismiss()
            }


            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))

        }
        arguments = null


    }

    private fun showInternetConnectionDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.network_daialog)
        val btnOK = dialog.findViewById<Button>(R.id.btnOk)
        btnOK.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun addToCalendar(context: Context) {
        arguments = null
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            context,
            { datePicker, selectedYear, selectedMonth, selectedDay ->
                val timePickerDialog = TimePickerDialog(
                    context,
                    { timePicker, selectedHour, selectedMinute ->

                        date =
                            "$selectedYear-${selectedMonth + 1}-$selectedDay $selectedHour:$selectedMinute"

                        calendar.set(
                            selectedYear,
                            selectedMonth,
                            selectedDay,
                            selectedHour,
                            selectedMinute
                        )
                        tvDate.text = date
                        dialog.show()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )

                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 7)
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.show()

    }

    fun getCityName(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context)
        var cityName: String? = null
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.let {
                if (it.isNotEmpty()) {
                    cityName = it[0].locality
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cityName
    }

    override fun deleteItem(alertWeather: AlertWeather) {
        viewModel.deleteAlertWeather(alertWeather)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getInitialDelayForFutureDate(futureDateStr: String): Long {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateTimeString = futureDateStr
        val dateTime: Date = dateTimeFormat.parse(dateTimeString) ?: Date()

        return dateTime.time
    }

    fun showEnableDialog() {
        if (sViewModel.getNotificationAccess() == "enable")

            showInternetConnectionDialog()
        else {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.notification_daialog)
            val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
            val btnEnable = dialog.findViewById<Button>(R.id.btnEnable)
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnEnable.setOnClickListener {
                sViewModel.setNotificationAccess("enable")
                dialog.dismiss()
            }
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))
            dialog.show()

        }
    }
}