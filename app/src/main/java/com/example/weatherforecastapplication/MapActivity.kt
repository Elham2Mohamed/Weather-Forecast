package com.example.weatherforecastapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherforecastapplication.home.HomeFragment
import com.google.android.gms.maps.model.LatLng

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val id = intent.getStringExtra("id")

        if (id != null) {
            val mapFragment = MAPFragment().apply {
                arguments = Bundle().apply {
                    putString("id", id)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.mapfragment, mapFragment)
                .commit()
        }
}
}