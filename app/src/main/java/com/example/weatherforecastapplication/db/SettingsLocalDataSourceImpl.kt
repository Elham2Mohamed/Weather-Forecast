package com.example.weatherforecastapplication.db

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

private const val PREFERENCES = "PREFERENCES"

class SettingsLocalDataSourceImpl  (var context: Context):SettingsLocalDataSource{
     private var sharedPreferences: SharedPreferences =context.applicationContext.getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor =sharedPreferences.edit()
    private val DATE="date"
    private val LANGUAGE="language"
    private val SPEED="speed"
    private val LOCATION="location"
    private val LONGITUDE="longitude"
    private val LATITUDE="latitude"
    private val TEMP="temp"
    private val NOTIFICATION="notification"
    init {
        if(!sharedPreferences.contains(LANGUAGE)){
            setLanguage("en")
            setSpeed("meter/sec")
            setLocation("gps")
            setTemp("kelvin")
            setNotificationAccess("enable")
        }
    }
    override fun setLanguage(lang:String){

        editor.putString(LANGUAGE, lang)
        editor.apply()
    }

    override fun setTemp(temp: String) {

        editor.putString(TEMP, temp)
        editor.apply()
    }

    override fun setDate(date: String) {

        editor.putString(DATE, date)
        editor.apply()
    }

    override fun setSpeed(speed: String) {

        editor.putString(SPEED, speed)
        editor.apply()
    }

    override fun setLocation(location: String) {

        editor.putString(LOCATION, location)
        editor.apply()
    }

    override fun setLongitude(lng: Double) {

        editor.putString(LONGITUDE, lng.toString())
        editor.apply()
    }

    override fun setLatitude(lat: Double) {

        editor.putString(LATITUDE, lat.toString())
        editor.apply()
    }

    override fun getTemp(): String {
        return sharedPreferences.getString(TEMP,"kelvin").toString()
    }

    override fun getLanguage(): String {
        return sharedPreferences.getString(LANGUAGE,"en").toString()
    }

    override fun getDate(): String {
         return sharedPreferences.getString(DATE,"").toString()

    }

    override fun getSpeed(): String {
         return sharedPreferences.getString(SPEED,"meter/sec").toString()

    }

    override fun getLocation(): String {

        return sharedPreferences.getString(LOCATION,"gps").toString()

    }

    override fun getLongitude(): Double {
        return sharedPreferences.getString(LONGITUDE,"0.0")!!.toDouble()

    }

    override fun getLatitude(): Double {
        return sharedPreferences.getString(LATITUDE,"0.0")!!.toDouble()

    }

    override fun getNotificationAccess(): String {
        return sharedPreferences.getString(NOTIFICATION,"enable").toString()
    }

    override fun setNotificationAccess(access: String) {
        editor.putString(NOTIFICATION, access)
        editor.apply()
    }
}