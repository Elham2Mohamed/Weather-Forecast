package com.example.weatherforecastapplication

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherforecastapplication.databinding.ActivityMainBinding
import com.example.weatherforecastapplication.db.AlertLocalDataSourceImpl
import com.example.weatherforecastapplication.db.SettingsLocalDataSourceImpl
import com.example.weatherforecastapplication.db.WeatherLocalDataSource
import com.example.weatherforecastapplication.home.view.HomeFragment
import com.example.weatherforecastapplication.model.RemoteDataSourceImpl
import com.example.weatherforecastapplication.model.WeatherRepositoryImp
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModel
import com.example.weatherforecastapplication.settings.viewModel.SettingsViewModelFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var sFactory: SettingsViewModelFactory
    private lateinit var sViewModel: SettingsViewModel
    lateinit var binding: ActivityMainBinding

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        sFactory = SettingsViewModelFactory(
            WeatherRepositoryImp.getInstance(
                RemoteDataSourceImpl(),
                WeatherLocalDataSource(this),
                SettingsLocalDataSourceImpl(this)
                ,AlertLocalDataSourceImpl(this)
            )
        )

        sViewModel = ViewModelProvider(this, sFactory).get(SettingsViewModel::class.java)


        if (intent.hasExtra("selectedLatLng")) {
            val selectedLatLng = intent.getParcelableExtra<LatLng>("selectedLatLng")
            val id = intent.getStringExtra("id")
            if (id != null) {
                if (id == "map") {
                    if (intent.getStringExtra("from") == "fav") {
                        val homeFragment = HomeFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable("selectedLatLng", selectedLatLng)
                                putString("fav", "fav")
                            }
                        }
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment_activity_main, homeFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_favorite,
                R.id.navigation_alerts,
                R.id.navigation_settings,
                R.id.navigation_map
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        lifecycleScope.launch(Dispatchers.IO) {
            sViewModel.language.collect { language ->
                val primaryLocale: Locale = this@MainActivity.resources.configuration.locales[0]
                val locale: String = primaryLocale.language
                Log.i("here", "onCreate: locale $locale language $language  ")
                if (!locale.equals(language)) {
                    var newLocale = Locale(sViewModel.getLanguage())
                    Locale.setDefault(newLocale)
                    val res = this@MainActivity.resources
                    val config: Configuration = (res.configuration)
                    config.setLocale(newLocale)
                    res.updateConfiguration(config, res.displayMetrics)
                    withContext(Dispatchers.Main) {
                        this@MainActivity.recreate()
                    }

                }

            }
        }
    }


}