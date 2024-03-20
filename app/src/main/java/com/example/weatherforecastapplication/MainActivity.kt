package com.example.weatherforecastapplication

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherforecastapplication.alerts.AlertsFragment
import com.example.weatherforecastapplication.databinding.ActivityMainBinding
import com.example.weatherforecastapplication.favorite.view.FavoriteFragment
import com.example.weatherforecastapplication.home.HomeFragment
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        if (intent.hasExtra("selectedLatLng")) {
            val selectedLatLng = intent.getParcelableExtra<LatLng>("selectedLatLng")
            val id = intent.getStringExtra("id")
            if (id != null) {
                if(id=="map"){
                    val homeFragment = HomeFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable("selectedLatLng", selectedLatLng)
                        }
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, homeFragment)
                        .addToBackStack(null)
                        .commit()
                }
               else if(id=="fav"){
                    val favFragment = FavoriteFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable("selectedLatLng", selectedLatLng)
                        }
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, favFragment)
                        .addToBackStack(null)
                        .commit()
                }
               else{
                    val alertFragment = AlertsFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable("selectedLatLng", selectedLatLng)
                        }
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, alertFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_favorite, R.id.navigation_alerts,R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}