package com.example.weatherapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.adapters.WeekWeatherAdapter
import com.example.weatherapp.databinding.FragmentWeekWeatherBinding
import com.example.weatherapp.model.forecast.Forecastday
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class WeekWeatherFragment : Fragment(), WeekWeatherAdapter.DayWeatherClickListener {
    private var _binding: FragmentWeekWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController
    private val viewModel: ForecastViewModel by viewModels()
    private lateinit var weekWeatherAdapter: WeekWeatherAdapter

    private val args by navArgs<WeekWeatherFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
        weekWeatherAdapter = WeekWeatherAdapter(requireContext(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWeekWeatherBinding.inflate(inflater, container, false)

        val forecastResponse = args.currentForecast

        if (forecastResponse != null) {
            Glide.with(this)
                .load("https:${forecastResponse.current.condition.icon}")
                .into(binding.ivWeatherIcon)

            binding.apply {
                tvWeatherDesc.text = "${forecastResponse.current.condition.text}"
                tvCloudValue.text = "${forecastResponse.current.cloud}%"
                tvWindSpeed.text = "${forecastResponse.current.wind_kph} KM/h"
                tvHumidity.text = "${forecastResponse.current.humidity}%"
            }


            val year = forecastResponse.location.localtime.substring(0, 4).toInt()
            val month = forecastResponse.location.localtime.substring(5, 7).toInt()
            val day = forecastResponse.location.localtime.substring(8, 10).toInt()

            val calendar = Calendar.getInstance()
            calendar.set(year, month, day) // set to a non-current date
            val (dayName, monthName) = getDayAndMonthName(calendar)

            binding.tvTodayDate.text = "$dayName, $monthName $day"


            // this set data in recyclerView of week weather
            setDataInRecyclerView(forecastResponse.forecast.forecastday)
        }


        binding.ivBack.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry ->
                mNavController.popBackStack(
                    backEntry.destination.id,
                    true
                )
            }
        }


        return binding.root
    }


    private fun setDataInRecyclerView(listForecastDay: List<Forecastday>) {

        binding.apply {
            rvWeekWeather.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvWeekWeather.setHasFixedSize(true)
            rvWeekWeather.adapter = weekWeatherAdapter
        }
        weekWeatherAdapter.updateListWeekWeather(listForecastDay as ArrayList<Forecastday>)
    }


    private fun getDayAndMonthName(calendar: Calendar): Pair<String, String> {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val month = calendar.get(Calendar.MONTH)
        val dayName = when (dayOfWeek) {
            Calendar.SUNDAY -> "Sun"
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tues"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thurs"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            else -> throw IllegalArgumentException("Invalid day of week: $dayOfWeek")
        }
        val monthName = when (month) {
            Calendar.JANUARY -> "January"
            Calendar.FEBRUARY -> "February"
            Calendar.MARCH -> "March"
            Calendar.APRIL -> "April"
            Calendar.MAY -> "May"
            Calendar.JUNE -> "June"
            Calendar.JULY -> "July"
            Calendar.AUGUST -> "August"
            Calendar.SEPTEMBER -> "September"
            Calendar.OCTOBER -> "October"
            Calendar.NOVEMBER -> "November"
            Calendar.DECEMBER -> "December"
            else -> throw IllegalArgumentException("Invalid month: $month")
        }
        return Pair(dayName, monthName)
    }

    override fun onItemClick(forecastDay: Forecastday) {

        val year = forecastDay.date.substring(0, 4).toInt()
        val month = forecastDay.date.substring(5, 7).toInt()
        val day = forecastDay.date.substring(8, 10).toInt()

        val calendar = Calendar.getInstance()

        calendar.set(year, calendar.get(Calendar.MONTH), day) // set to a non-current date
        val (dayName, monthName) = getDayAndMonthName(calendar)

        binding.tvTodayDate.text = "$dayName, $monthName $day"

        Glide.with(this)
            .load("https:${forecastDay.day.condition.icon}")
            .into(binding.ivWeatherIcon)

        binding.apply {
            tvWeatherDesc.text = "${forecastDay.day.condition.text}"
            tvCloudValue.text = "${forecastDay.day.daily_chance_of_rain}%"
            tvWindSpeed.text = "${forecastDay.day.maxwind_kph} KM/h"
            tvHumidity.text = "${forecastDay.day.avghumidity}%"
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}