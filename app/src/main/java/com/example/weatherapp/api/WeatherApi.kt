package com.example.weatherapp.api

import com.example.weatherapp.model.forecast.ForecastResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast.json")
    fun getForecastWeather(
        @Query("q") location: String,
        @Query("lang") languageCode: String = "en",
        @Query("days") days: Int
    ): Call<ForecastResponse>

}