package com.example.weatherforecastapplication.favorite.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.model.WeatherData

class FAVWeatherAdapter(
    private val onClickListener: FavOnClickListener, val context: FavoriteFragment
) : ListAdapter<WeatherData, FAVWeatherAdapter.ViewHolder>(WeatherDiffutil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current: WeatherData = getItem(position)
        holder.desc.text = current.list[0].weather[0].description
        holder.city.text = current.city?.name ?: "Cairo"
        holder.btnDelete.setOnClickListener { onClickListener.deleteItem(current) }
        holder.itemView.setOnClickListener { onClickListener.onClickItem(current) }
        when {
            context.speed == "meter/sec" && context.temp == "fahrenheit" -> {
                if (context.sViewModel.getTemp() != "fahrenheit")
                    holder.degree.text =
                        context.convertKelvinToFahrenheit(current.list[0].main.temp).toInt()
                            .toString() + "°F"
                else
                    holder.degree.text = (current.list[0].main.temp).toInt().toString() + "°F"
            }

            context.speed == "miles/hour" && context.temp == "celsius" -> {
                if (context.sViewModel.getTemp() != "celsius") {
                    holder.degree.text =
                        context.convertKelvinToCelsius(current.list[0].main.temp).toInt()
                            .toString() + "°C"
                } else
                    holder.degree.text = (current.list[0].main.temp).toInt().toString() + "°C"


            }

            context.speed == "miles/hour" && context.temp == "kelvin" -> {
                holder.degree.text = current.list[0].main.temp.toString() + "°K"

            }

            context.speed == "miles/hour" && context.temp == "fahrenheit" -> {
                if (context.sViewModel.getTemp() != "fahrenheit") {
                    holder.degree.text =
                        context.convertKelvinToFahrenheit(current.list[0].main.temp).toInt()
                            .toString() + "°F"
                } else
                    holder.degree.text = (current.list[0].main.temp).toInt().toString() + "°F"

            }

            (context.speed == "meter/sec" && context.temp == "celsius") -> {
                if (context.sViewModel.getTemp() != "celsius")
                    holder.degree.text =
                        context.convertKelvinToCelsius(current.list[0].main.temp).toInt()
                            .toString() + "°C"
                else
                    holder.degree.text = (current.list[0].main.temp).toInt().toString() + "°C"

            }

            else -> {
                holder.degree.text = current.list[0].main.temp.toString() + "°K"
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val degree: TextView = itemView.findViewById(R.id.tvDegree)
        val city: TextView = itemView.findViewById(R.id.tvCity)
        val desc: TextView = itemView.findViewById(R.id.tvDesc)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

    }

}