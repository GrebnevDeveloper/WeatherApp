package com.grebnev.weatherapp.data.network.dto

import com.google.gson.annotations.SerializedName

data class ForecastDayDto(
    @SerializedName("forecastday") val forecastDays: List<DayDto>
)