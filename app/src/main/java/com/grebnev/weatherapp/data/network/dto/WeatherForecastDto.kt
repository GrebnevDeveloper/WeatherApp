package com.grebnev.weatherapp.data.network.dto

import com.google.gson.annotations.SerializedName

data class WeatherForecastDto(
    @SerializedName("current") val weatherCurrent: WeatherDto,
    @SerializedName("forecast") val weatherForecast: ForecastDayDto,
)
