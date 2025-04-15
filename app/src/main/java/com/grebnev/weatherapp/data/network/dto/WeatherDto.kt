package com.grebnev.weatherapp.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    @SerialName("last_updated_epoch") val date: Long,
    @SerialName("temp_c") val tempC: Float,
    @SerialName("condition") val condition: ConditionDto,
)