package com.example.weatherforecastapplication.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weatherforecastapplication.MainActivity
import com.example.weatherforecastapplication.R

private const val CHANNEL_ID:String="CHANNEL_ID"
fun createNotification(context: Context,desc:String,city:String) {
    val notificationManager =
        context.getSystemService(NotificationManager::class.java) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Notification Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(CHANNEL_ID, name, importance)

        notificationManager.createNotificationChannel(channel)
    }
    val builder= NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.notifications)
        .setContentTitle("Current weather in $city ")
        .setContentText(desc)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(123,builder)

}