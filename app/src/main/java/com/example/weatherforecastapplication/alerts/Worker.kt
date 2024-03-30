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
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.network.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val NOTIFICATION_PERM=1
open class Worker(context: Context, params:WorkerParameters):Worker(context,params){
    private  val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    lateinit var dialog: Dialog
    lateinit var tvDesc:TextView
    @SuppressLint("LogNotTimber")
    override fun doWork(): Result {
        Log.i("TAG", "doWork: enter1 ")

        val lat=inputData.getDouble("lat",0.0)
        val log=inputData.getDouble("log",0.0)
        val lang=inputData.getString("lang")
        val type=inputData.getString("type")
        val access=inputData.getString("access")
        Log.i("TAG", "doWork: lat $lat ,log $log ,lang $lang ,type $type ")
         val retrofit=Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).
         baseUrl(BASE_URL)
         .build()

        val api=retrofit.create(WeatherService::class.java)

        var response:WeatherData?=null

        runBlocking {
            response = lang?.let {
                api.getCurrentWeatherWithoutUints(
                    lat = lat, lon = log, lang = it,"39b555dfd3fff6499b8c963689da8e9c"
                )
            }
        }

        if(response!=null){
            Log.i("TAG", "doWork: enter2 ")

            if(type=="Notifications"){

                   val notificationManager=applicationContext.getSystemService(NotificationManager::class.java)as NotificationManager
                   if(notificationManager.areNotificationsEnabled()){
                       Log.i("TAG", "doWork: enter3 ")
                       response!!.city?.name?.let {
                           createNotification(applicationContext,
                               response!!.list[0].weather[0].description, it
                           )
                       }

                       return Result.success()
                   }
                   else{
                       Log.i("TAG", "doWork: enter 4")

                       return Result.failure()
                   }

           }
            else{
                Log.i("TAG", "doWork: enter 5")

                if(access=="enable"){
                    Log.i("TAG", "doWork: enter 6")

                    runBlocking {
                         withContext(Dispatchers.Main){
                             val icon = response!!.list[0].weather[0].icon
                             val city=response!!.city?.name
                             val desc = response!!.list[0].weather[0].description
                             showAlertDialog(applicationContext.applicationContext)
                             //  city?.let { displayDialog(desc, it,icon) }
                         }
                     }

                }
               else{
                    Log.i("TAG", "doWork: enter 7")

                    return Result.failure()
               }


            }

        }
        else
        {
            Log.i("TAG", "doWork: enter 8")

            return Result.failure()
        }


        return Result.success()

    }

    private fun showAlertDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert Dialog")
        builder.setMessage("This is an example of an alert dialog.")

        // Set positive button with its action
        builder.setPositiveButton("OK") { dialog, which ->
            // Action to perform when OK button is clicked
            dialog.dismiss() // Dismiss the dialog
        }

        // Set negative button with its action
        builder.setNegativeButton("Cancel") { dialog, which ->
            // Action to perform when Cancel button is clicked
            dialog.dismiss() // Dismiss the dialog
        }

        val alertDialog = builder.create()
        alertDialog.window?.apply {
            setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        }
        alertDialog.show()
    }


    private fun displayDialog(desc:String,city:String,icon:String) {
        dialog = Dialog(applicationContext)
        dialog.setContentView(R.layout.alert_daialog)
        val btnOK = dialog.findViewById<Button>(R.id.btnOk)
        tvDesc=dialog.findViewById<TextView>(R.id.tvDesc)
         val imgIcon=dialog.findViewById<ImageView>(R.id.icon)
        val dCity=when(city) {
            "Mountain View" -> "Cairo"
            "ماونتن فيو" -> "القاهرة"
            else -> city
        }

        tvDesc.text=" $desc, in $dCity"
        imgIcon.setImageResource(getImage(icon))

        btnOK.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))

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