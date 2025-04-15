package com.grebnev.weatherapp.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("country") val country: String,
)