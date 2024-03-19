package com.example.weatherforecastapplication.home

import android.annotation.SuppressLint
import android.media.Rating
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.model.WeatherData
import com.example.weatherforecastapplication.model.WeatherEntry
import com.squareup.picasso.Picasso
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyAdapter(
    private val context:HomeFragment
    //private val onItemClick: (WeatherEntry) -> Unit
) : ListAdapter<WeatherEntry, DailyAdapter.ViewHolder>(WeatherDiffutil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current: WeatherEntry = getItem(position)
        val dtTxt = current.dt_txt
        val parts = dtTxt.split(" ")
        val dateString = parts[0]
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, formatter)
        val dayOfWeek = date.dayOfWeek

        val icon=context.getImage(current.weather[0].icon)
        holder.icon.setImageResource(icon)
        holder.tvDate.text = dayOfWeek.toString()+"\n"+parts[0]
        holder.tvDesc.text=current.weather[0].description
        when {
            context.speed == "meter/sec" &&  context.temp == "Fahrenheit" -> {
                holder.tvDegree.text = context.celsiusToFahrenheit(current.main.temp).toString()+ "°F"

            }
            context.speed == "miles/hour" &&  context.temp == "Celsius" -> {
                holder.tvDegree.text = context.fahrenheitToCelsius(current.main.temp).toString()+ "°C"

            }
            context. speed == "miles/hour" &&  context.temp == "Kelvin" -> {
                holder.tvDegree.text = current.main.temp.toString() + "°K"

            }
            context.speed=="miles/hour" &&  context.temp == "Fahrenheit" ->{
                holder. tvDegree.text = current.main.temp.toString() + "°F"

            }
            ( context.speed=="meter/sec"&& context.temp=="Celsius")->{
                holder.tvDegree.text = current.main.temp.toString() + "°C"

            }

            else->{
                holder. tvDegree.text = current.main.temp.toString() + "°K"
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDegree: TextView = itemView.findViewById(R.id.tvDegree)
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)

    }
}
