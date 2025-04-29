package com.grebnev.weatherapp.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherForecastDto(
    @SerialName("current") val weatherCurrent: WeatherDto,
    @SerialName("forecast") val weatherForecast: ForecastDayDto,
)