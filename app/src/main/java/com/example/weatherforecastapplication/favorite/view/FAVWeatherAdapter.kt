package com.example.weatherforecastapplication.favorite.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.model.WeatherData

class FAVWeatherAdapter (private val onClickListener: FavOnClickListener,val context: FavoriteFragment
) : ListAdapter<WeatherData, FAVWeatherAdapter.ViewHolder>(WeatherDiffutil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current: WeatherData = getItem(position)
        holder.degree.text = current.list[0].main.temp.toString()
        holder.desc.text = current.list[0].weather[0].description
        holder.city.text = current.city?.name ?: "Cairo"
      //  holder.icon.setImageResource(getImage(current.list[0].weather[0].icon))
        holder.btnDelete.setOnClickListener { onClickListener.deleteItem(current) }
        holder.itemView.setOnClickListener { onClickListener.onClickItem(current) }
        when {
            context.speed == "meter/sec" && context.temp == "Fahrenheit" -> {
                holder.degree.text =
                    context.celsiusToFahrenheit(current.list[0].main.temp).toString() + "°F"

            }

            context.speed == "miles/hour" && context.temp == "Celsius" -> {
                holder.degree.text =
                    context.fahrenheitToCelsius(current.list[0].main.temp).toString() + "°C"

            }

            context.speed == "miles/hour" && context.temp == "Kelvin" -> {
                holder.degree.text = current.list[0].main.temp.toString() + "°K"

            }

            context.speed == "miles/hour" && context.temp == "Fahrenheit" -> {
                holder.degree.text = current.list[0].main.temp.toString() + "°F"

            }

            (context.speed == "meter/sec" && context.temp == "Celsius") -> {
                holder.degree.text = current.list[0].main.temp.toString() + "°C"

            }

            else -> {
                holder.degree.text = current.list[0].main.temp.toString() + "°K"
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val degree: TextView = itemView.findViewById(R.id.tvDegree)
        val city: TextView = itemView.findViewById(R.id.tvCity)
        val  desc: TextView = itemView.findViewById(R.id.tvDesc)
        val btnDelete: ImageButton =itemView.findViewById(R.id.btnDelete)

    }
    fun getImage(image: String): Int {
        when (image) {
            "01d" -> return R.drawable.cleard
            "01n" -> return R.drawable.clearn
            "02d" -> return R.drawable.fewd
            "20n" -> return R.drawable.fewn
            "10d" -> return R.drawable.raind
            "10n" -> return R.drawable.rainn
            "03d", "03n" -> return R.drawable.scatteredd
            "04d", "04n" -> return R.drawable.brokenn
            "09d", "09n" -> return R.drawable.showerd
            "11d", "11n" -> return R.drawable.thunderstormn
            "13d", "13n" -> return R.drawable.snowd
            "50d", "50n" -> return R.drawable.mistn
        }
        return R.drawable.cloudy
    }
}