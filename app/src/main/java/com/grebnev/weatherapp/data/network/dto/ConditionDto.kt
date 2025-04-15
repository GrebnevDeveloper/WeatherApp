package com.grebnev.weatherapp.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConditionDto(
    @SerialName("text") val text: String,
    @SerialName("icon") val iconUrl: String,
)