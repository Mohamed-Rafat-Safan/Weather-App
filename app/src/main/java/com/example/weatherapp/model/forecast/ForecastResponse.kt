package com.example.weatherapp.model.forecast

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ForecastResponse(
    val current: Current,
    val forecast: Forecast,
    val location: Location
):Parcelable