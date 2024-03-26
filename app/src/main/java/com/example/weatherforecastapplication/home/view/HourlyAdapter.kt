package com.example.weatherforecastapplication.home.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.model.WeatherEntry

class HourlyAdapter(
    private val context: HomeFragment
) : ListAdapter<WeatherEntry, HourlyAdapter.ViewHolder>(WeatherDiffutil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current: WeatherEntry = getItem(position)
        val icon = context.getImage(current.weather[0].icon)
        holder.icon.setImageResource(icon)
        val dtTxt = current.dt_txt
        val parts = dtTxt.split(" ")
        val part = parts[1].split(":")

        val formattedDate = if (context.language == "ar") {
            if (part[0].compareTo("12") > 0)
                holder.tvDate.text = "${part[0]}:00 م"
            else
                holder.tvDate.text = "${part[0]}:00 ص"
        } else {
            if (part[0].compareTo("12") > 0)
                holder.tvDate.text = part[0] + ":00 PM"
            else
                holder.tvDate.text = part[0] + ":00 AM"
        }
        when {
            context.speed == "meter/sec" && context.temp == "fahrenheit" -> {
                if (context.sViewModel.getTemp() != "fahrenheit")
                    holder.tvDegree.text =
                        context.convertKelvinToFahrenheit(current.main.temp).toInt()
                            .toString() + "°F"
                else
                    holder.tvDegree.text = (current.main.temp).toInt().toString() + "°F"

            }

            context.speed == "miles/hour" && context.temp == "celsius" -> {
                if (context.sViewModel.getTemp() != "celsius") {
                    holder.tvDegree.text =
                        context.convertKelvinToCelsius(current.main.temp).toInt().toString() + "°C"
                } else
                    holder.tvDegree.text = (current.main.temp).toInt().toString() + "°C"

            }

            context.speed == "miles/hour" && context.temp == "kelvin" -> {
                holder.tvDegree.text = current.main.temp.toString() + "°K"

            }

            context.speed == "miles/hour" && context.temp == "fahrenheit" -> {
                if (context.sViewModel.getTemp() != "fahrenheit")
                    holder.tvDegree.text =
                        context.convertKelvinToFahrenheit(current.main.temp).toInt()
                            .toString() + "°F"
                else
                    holder.tvDegree.text = (current.main.temp).toInt().toString() + "°F"
            }

            (context.speed == "meter/sec" && context.temp == "celsius") -> {
                if (context.sViewModel.getTemp() != "celsius")
                    holder.tvDegree.text =
                        context.convertKelvinToCelsius(current.main.temp).toInt().toString() + "°C"
                else
                    holder.tvDegree.text = (current.main.temp).toInt().toString() + "°C"

            }

            else -> {
                holder.tvDegree.text = current.main.temp.toString() + "°K"
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDegree: TextView = itemView.findViewById(R.id.tvDegree)
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val tvDate: TextView = itemView.findViewById(R.id.tvHoure)

    }
}
