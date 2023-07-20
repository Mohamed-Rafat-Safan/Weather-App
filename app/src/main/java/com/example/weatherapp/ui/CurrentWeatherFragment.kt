package com.example.weatherapp.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.adapters.HourlyAdapter
import com.example.weatherapp.databinding.FragmentCurrentWeatherBinding
import com.example.weatherapp.model.forecast.ForecastResponse
import com.example.weatherapp.model.forecast.Hour
import com.example.weatherapp.utils.Constants.DAY_NUMBER
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE



@AndroidEntryPoint
class CurrentWeatherFragment : Fragment() {
    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController
    private val viewModel: ForecastViewModel by viewModels()
    private lateinit var forecastResponse: ForecastResponse
    private lateinit var hourlyAdapter: HourlyAdapter

    private var  retrieveData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
        hourlyAdapter = HourlyAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)


        viewModel.getForecastWeather("Egypt", "en", DAY_NUMBER)
        viewModel.forecastWeather.observe(viewLifecycleOwner) {
            forecastResponse = it

            val year = it.location.localtime.substring(0, 4).toInt()
            val month = it.location.localtime.substring(5, 7).toInt()
            val day = it.location.localtime.substring(8, 10).toInt()

            val calendar = Calendar.getInstance()

            calendar.set(year, calendar.get(Calendar.MONTH), day) // set to a non-current date

            val (dayName, monthName) = getDayAndMonthName(calendar)

            binding.tvTodayDate.text = "$dayName, $monthName $day"

            binding.tvLocation.text = "${it.location.country}, ${it.location.name}"

            Glide.with(this)
                .load("https:${it.current.condition.icon}")
                .into(binding.ivCurrentWeatherIcon)

            binding.apply {
                tvWeatherDesc.text = "${it.current.condition.text}"
                tvTemperature.text = "${it.current.temp_c.toInt()}°C"
                tvCloudValue.text = "${it.current.cloud}%"
                tvWindSpeed.text = "${it.current.wind_kph} KM/h"
                tvHumidity.text = "${it.current.humidity}%"
            }


            val todayData = it.forecast.forecastday[0]
            binding.apply {
                tvMaxWeather.text = "Max: ${todayData.day.maxtemp_c}°C"
                tvMinWeather.text = "Min: ${todayData.day.mintemp_c}°C"
            }

            // this set data in recyclerView of hour weather
            setDataInRecyclerView(todayData.hour)

            retrieveData = true
        }



        binding.tvWeekWeather.setOnClickListener {
            if (hasInternetConnection(requireContext()) && retrieveData) {
                val action =
                    CurrentWeatherFragmentDirections.actionCurrentWeatherFragmentToFutureListWeatherFragment(
                        forecastResponse
                    )
                mNavController.navigate(action)
            } else {
                val action =
                    CurrentWeatherFragmentDirections.actionCurrentWeatherFragmentToFutureListWeatherFragment(
                        null
                    )
                mNavController.navigate(action)
            }

        }


        return binding.root
    }


    private fun setDataInRecyclerView(listHourly: List<Hour>) {

        binding.apply {
            rvTodayWeather.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvTodayWeather.setHasFixedSize(true)
            rvTodayWeather.adapter = hourlyAdapter
        }
        hourlyAdapter.updateListHourlyWeather(listHourly as ArrayList<Hour>)
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


    fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }

        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

