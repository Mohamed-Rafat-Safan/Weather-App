package com.example.weatherapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.ItemHourlyWeatherBinding
import com.example.weatherapp.model.forecast.Hour

class HourlyAdapter(val context: Context) : RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    private var listHourlyWeather = ArrayList<Hour>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val binding =
            ItemHourlyWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listHourlyWeather.size
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourWeather = listHourlyWeather[position]

        holder.bind(hourWeather)

        holder.itemView.setOnClickListener {

        }
    }


    fun updateListHourlyWeather(listHourlyWeatherNew: ArrayList<Hour>) {
        listHourlyWeather = listHourlyWeatherNew
        notifyDataSetChanged()
    }

    inner class HourlyViewHolder(val binding: ItemHourlyWeatherBinding) : ViewHolder(binding.root) {
        fun bind(hour: Hour) {
            binding.apply {
                val time = hour.time.substring(11, 13).toInt()
                if (time in 0..11) {
                    tvTime.text = "${if (time == 0) "12" else time} am"
                }else{
                    tvTime.text = "${if (time == 12) "12" else (time-12)} pm"
                }


                Glide.with(context)
                    .load("https:${hour.condition.icon}")
                    .into(binding.ivWeatherIcon)

                tvWeatherInTime.text = "${hour.temp_c.toString()}°C"
            }
        }
    }

}