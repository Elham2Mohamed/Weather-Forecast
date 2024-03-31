package com.example.weatherforecastapplication.alerts

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.network.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val NOTIFICATION_PERM = 1

open class Worker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    lateinit var dialog: Dialog
    lateinit var tvDesc: TextView
    lateinit var lang: String

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("LogNotTimber")
    override fun doWork(): Result {
        Log.i("TAG", "doWork: enter1 ")

        val lat = inputData.getDouble("lat", 0.0)
        val log = inputData.getDouble("log", 0.0)
        lang = inputData.getString("lang").toString()
        val type = inputData.getString("type")
        val date = inputData.getString("date")
        val city = inputData.getString("city")
        val id = inputData.getInt("id", 0)

        val access = inputData.getString("access")
        Log.i("TAG", "doWork: lat $lat ,log $log ,lang $lang ,type $type , iD :$id")

        val retrofit =
            Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL)
                .build()

        val api = retrofit.create(WeatherService::class.java)

        var response: WeatherData? = null

        runBlocking {
            response = lang?.let {
                api.getCurrentWeatherWithoutUints(
                    lat = lat, lon = log, lang = it, "39b555dfd3fff6499b8c963689da8e9c"
                )
            }
        }

        if (response != null) {
            Log.i("TAG", "doWork: enter2 ")

            if (type == "Notifications") {

                val notificationManager =
                    applicationContext.getSystemService(NotificationManager::class.java) as NotificationManager
                if (notificationManager.areNotificationsEnabled()) {
                    Log.i("TAG", "doWork: enter3 ")
                    val outputData = workDataOf(
                        "city" to city,
                        "date" to date,
                        "lat" to lat,
                        "lon" to log,
                        "type" to type,
                        "id" to id
                    )
                    Log.i("TAG", "doWork:notAlert $outputData")
                    response!!.city?.name?.let {
                        createNotification(
                            applicationContext,
                            response!!.list[0].weather[0].description, it
                        )
                    }
                    return Result.success(outputData)

                } else {
                    Log.i("TAG", "doWork: enter 4")

                    return Result.failure()
                }

            } else {
                Log.i("TAG", "doWork: enter 5")

                if (access == "enable") {
                    Log.i("TAG", "doWork: enter 6")

                    runBlocking {
                        withContext(Dispatchers.Main) {
                            val icon = response!!.list[0].weather[0].icon
                            val city = response!!.city?.name
                            val desc = response!!.list[0].weather[0].description
                            city?.let {
                                showAlertDialog(
                                    applicationContext.applicationContext, desc,
                                    it, icon
                                )
                            }


                        }
                    }
                    val outputData = workDataOf(
                        "city" to city,
                        "date" to date,
                        "lat" to lat,
                        "lon" to log,
                        "type" to type,
                        "id" to id
                    )

                    return Result.success(outputData)

                } else {
                    Log.i("TAG", "doWork: enter 7")

                    return Result.failure()
                }


            }

        } else {
            showInternetConnectionDialog()
            Log.i("TAG", "doWork: enter 8")

            return Result.failure()
        }

        return Result.success()
    }

    private fun showInternetConnectionDialog() {
        val dialog = Dialog(applicationContext)
        dialog.setContentView(R.layout.network_daialog)
        val btnOK = dialog.findViewById<Button>(R.id.btnOk)
        btnOK.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAlertDialog(context: Context, desc: String, city: String, icon: String) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.alarm_daialog, null)

        val tvDesc = view.findViewById<TextView>(R.id.tvDesc)
        val imgIcon = view.findViewById<ImageView>(R.id.icon)
        val btnOK = view.findViewById<Button>(R.id.btnOk)

        val dCity = when (city) {
            "Mountain View" -> "Cairo"
            "ماونتن فيو" -> "القاهرة"
            else -> city
        }
        tvDesc.text  = when (lang) {
            "en" ->  "Current weather in $dCity \n $desc"
            "ar" ->  "الطقس الحالي في $dCity \n  $desc"
            else ->  "Current weather in $dCity \n $desc"
        }

        imgIcon.setImageResource(getImage(icon))
        val dialog = AlertDialog.Builder(context).setView(view).create()
        // Set onClickListener for the OK button to dismiss the dialog
        btnOK.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)

        dialog.show()
    }


    fun getImage(image: String): Int {
        when (image) {
            "01d" -> return R.drawable.cleard
            "01n" -> return R.drawable.clearn
            "02d" -> return R.drawable.fewn
            "20n" -> return R.drawable.fewd
            "10d" -> return R.drawable.raind
            "10n" -> return R.drawable.rainn
            "03d", "03n" -> return R.drawable.scatteredd
            "04d", "04n" -> return R.drawable.brokenn
            "09d", "09n" -> return R.drawable.showerd
            "11d", "11n" -> return R.drawable.thunderstormn
            "13d", "13n" -> return R.drawable.snowd
            "50d", "50n" -> return R.drawable.mistn
        }
        return R.drawable.cloudy
    }
}