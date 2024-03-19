package com.example.weatherforecastapplication.home

import android.annotation.SuppressLint
import android.media.Rating
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherEntry
import com.squareup.picasso.Picasso

class HourlyAdapter(
    private val context:HomeFragment
   // private val onItemClick: (WeatherEntry) -> Unit
) : ListAdapter<WeatherEntry, HourlyAdapter.ViewHolder>(WeatherDiffutil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current: WeatherEntry = getItem(position)
        val icon=context.getImage(current.weather[0].icon)
        holder.icon.setImageResource(icon)
         val dtTxt = current.dt_txt
        val parts = dtTxt.split(" ")
         val part = parts[1].split(":")
        if(part[0].compareTo("12")>0)
        holder.tvDate.text = part[0]+":00 PM"
        else
            holder.tvDate.text = part[0]+":00 AM"
        when {
            context.speed == "meter/sec" && context.temp == "Fahrenheit" -> {
                holder.tvDegree.text =
                    context.celsiusToFahrenheit(current.main.temp).toString() + "°F"

            }

            context.speed == "miles/hour" && context.temp == "Celsius" -> {
                holder.tvDegree.text =
                    context.fahrenheitToCelsius(current.main.temp).toString() + "°C"

            }

            context.speed == "miles/hour" && context.temp == "Kelvin" -> {
                holder.tvDegree.text = current.main.temp.toString() + "°K"

            }

            context.speed == "miles/hour" && context.temp == "Fahrenheit" -> {
                holder.tvDegree.text = current.main.temp.toString() + "°F"

            }

            (context.speed == "meter/sec" && context.temp == "Celsius") -> {
                holder.tvDegree.text = current.main.temp.toString() + "°C"

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
