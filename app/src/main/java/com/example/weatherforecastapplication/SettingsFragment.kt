package com.example.weatherforecastapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.weatherforecastapplication.R
import com.google.android.gms.maps.MapFragment

class SettingsFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
     private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val rgLanguages = view.findViewById<RadioGroup>(R.id.rgLanguages)
        val rgLocation = view.findViewById<RadioGroup>(R.id.rgLocation)
        val rgTemperature = view.findViewById<RadioGroup>(R.id.rgTemperature)
        val rgSpeed = view.findViewById<RadioGroup>(R.id.rgSpeed)
        val rgNotifications = view.findViewById<RadioGroup>(R.id.rgNotifications)

        rgLanguages.setOnCheckedChangeListener { _, checkedId ->
            editor.putInt(Languages, checkedId)
            editor.apply()
        }

        rgLocation.setOnCheckedChangeListener { _, checkedId ->
            editor.putInt(Location, checkedId)
            editor.apply()
            if (checkedId == R.id.rBtn2Location) {
                val intent=Intent(context,MapActivity::class.java)
                    .putExtra("id","map")
                startActivity(intent)

            }
        }

        rgTemperature.setOnCheckedChangeListener { _, checkedId ->
            editor.putInt(Temperature, checkedId)
            editor.apply()
        }

        rgSpeed.setOnCheckedChangeListener { _, checkedId ->
            editor.putInt(Speed, checkedId)
            editor.apply()
        }

        rgNotifications.setOnCheckedChangeListener { _, checkedId ->
            editor.putInt(Notifications, checkedId)
            editor.apply()

        }
        editor.commit()

    }

    companion object {
        private const val PREFERENCES = "PREFERENCES"
        private const val Location = "Location"
        private const val Languages = "Languages"
        private const val Speed = "Speed"
        private const val Temperature = "Temperature"
        private const val Notifications = "Notifications"
    }
}
