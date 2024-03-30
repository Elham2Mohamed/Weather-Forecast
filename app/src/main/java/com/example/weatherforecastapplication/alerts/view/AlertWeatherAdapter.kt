package com.example.weatherforecastapplication.alerts.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
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
import com.example.weatherforecastapplication.model.AlertWeather

class AlertWeatherAdapter(
    private val onClickListener: AlertOnClickListener
) :ListAdapter<AlertWeather, AlertWeatherAdapter.ViewHolder>(WeatherDiffutil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.alert_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current: AlertWeather = getItem(position)
        holder.city.text = current.city
        holder.date.text=current.date
        holder.btnDelete.setOnClickListener {
            val  dialog = Dialog(holder.itemView.context)
            dialog.setContentView(R.layout.delete_daialog)
            val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
            val btnDelete = dialog.findViewById<Button>(R.id.btnDelete)
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnDelete.setOnClickListener {
                onClickListener.deleteItem(current)
                dialog.dismiss()
            }
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))
            dialog.show()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val city: TextView = itemView.findViewById(R.id.tvCity)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

}

