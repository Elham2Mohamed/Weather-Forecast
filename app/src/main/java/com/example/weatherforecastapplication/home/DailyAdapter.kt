package com.example.weatherforecastapplication.home

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

class DailyAdapter(
    private val onItemClick: (WeatherEntry) -> Unit
) : ListAdapter<WeatherEntry, DailyAdapter.ViewHolder>(WeatherDiffutil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current: WeatherEntry = getItem(position)
        val dtTxt = current.dt_txt
        val parts = dtTxt.split(" ")
        // Picasso.get().load(current.thumbnailUrl).into(holder.productImage)
        holder.tvDegree.text = current.wind.deg.toString()+"°C"
        holder.tvDate.text = parts[0]
        holder.tvDesc.text=current.weather[0].description
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDegree: TextView = itemView.findViewById(R.id.tvDegree)
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)

    }
}
