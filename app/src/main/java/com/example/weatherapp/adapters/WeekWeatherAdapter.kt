package com.example.weatherapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.ItemWeekWeatherBinding
import com.example.weatherapp.model.forecast.Forecastday
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList

class WeekWeatherAdapter(
    val context: Context,
    val dayWeatherClickListener: DayWeatherClickListener
) :
    RecyclerView.Adapter<WeekWeatherAdapter.WeekWeatherViewHolder>() {

    private var listWeekWeather = ArrayList<Forecastday>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekWeatherViewHolder {
        val binding =
            ItemWeekWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeekWeatherViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listWeekWeather.size
    }

    override fun onBindViewHolder(holder: WeekWeatherViewHolder, position: Int) {
        val forecastDay = listWeekWeather[position]

        holder.bind(forecastDay)

        holder.itemView.setOnClickListener {
            dayWeatherClickListener.onItemClick(forecastDay)
        }
    }


    fun updateListWeekWeather(listWeekWeatherNew: ArrayList<Forecastday>) {
        listWeekWeather = listWeekWeatherNew
        notifyDataSetChanged()
    }


    fun getDayName(calendar: Calendar): String {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> throw IllegalArgumentException("Invalid day of week: $dayOfWeek")
        }
    }



    inner class WeekWeatherViewHolder(val binding: ItemWeekWeatherBinding) :
        ViewHolder(binding.root) {
        val calendar = Calendar.getInstance()
        fun bind(forecastDay: Forecastday) {

            val year = forecastDay.date.substring(0, 4).toInt()
            val month = forecastDay.date.substring(5, 7).toInt()
            val day = forecastDay.date.substring(8, 10).toInt()
            calendar.set(year, calendar.get(Calendar.MONTH), day) // set to a non-current date
            val dayName = getDayName(calendar)

            binding.tvDayName.text = "$dayName"

            Glide.with(context).load("https:${forecastDay.day.condition.icon}")
                .into(binding.ivWeatherIcon)

            binding.tvWeatherDesc.text = forecastDay.day.condition.text

            binding.tvMax.text = "${forecastDay.day.maxtemp_c}°C"
            binding.tvMin.text = "${forecastDay.day.mintemp_c}°C"
        }
    }


    interface DayWeatherClickListener {
        fun onItemClick(forecastDay: Forecastday)
    }


}