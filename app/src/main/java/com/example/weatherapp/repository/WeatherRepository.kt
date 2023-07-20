package com.example.weatherapp.repository

import com.example.weatherapp.api.WeatherApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherApi: WeatherApi) {

    suspend fun getForecastWeather(loc: String, language: String, dayNum: Int) =
        weatherApi.getForecastWeather(location = loc, languageCode = language, days = dayNum)

}