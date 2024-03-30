package com.example.weatherforecastapplication.db

interface SettingsLocalDataSource {
    abstract fun setLanguage(lang: String)
    abstract  fun setTemp(lang:String)
    abstract  fun setDate(date:String)
    abstract  fun setSpeed(speed:String)
    abstract  fun setLocation(location:String)
    abstract  fun setLongitude(lng:Double)
    abstract  fun setLatitude(lat:Double)
    abstract  fun getTemp():String

    abstract  fun getLanguage():String
    abstract  fun getDate():String
    abstract  fun getSpeed():String
    abstract  fun getLocation():String
    abstract  fun getLongitude():Double
    abstract  fun getLatitude():Double
    abstract fun getNotificationAccess(): String
    abstract fun setNotificationAccess(access: String)

}