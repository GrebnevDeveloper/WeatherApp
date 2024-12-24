package com.grebnev.weatherapp.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Long,
    val name: String,
    val country: String
)
