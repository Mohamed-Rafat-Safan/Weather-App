package com.example.weatherapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.forecast.ForecastResponse
import com.example.weatherapp.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    private val _forecastWeather = MutableLiveData<ForecastResponse>()
    val forecastWeather: LiveData<ForecastResponse>
        get() = _forecastWeather


    fun getForecastWeather(location: String, language: String, dayNum: Int) =
        viewModelScope.launch {
            val call: Call<ForecastResponse> =
                repository.getForecastWeather(location, language, dayNum)
            call.enqueue(object : Callback<ForecastResponse?> {
                override fun onResponse(
                    call: Call<ForecastResponse?>,
                    response: Response<ForecastResponse?>
                ) {
                    _forecastWeather.value = response.body()
                }

                override fun onFailure(call: Call<ForecastResponse?>, t: Throwable) {
                    Log.e("ResponseError", "Fail to get the data..")
                }
            })
        }


}