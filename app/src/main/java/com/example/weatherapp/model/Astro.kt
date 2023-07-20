package com.example.weatherapp.model.forecast

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Astro(
    val is_moon_up: Int,
    val is_sun_up: Int,
    val moon_illumination: String,
    val moon_phase: String,
    val moonrise: String,
    val moonset: String,
    val sunrise: String,
    val sunset: String
):Parcelable