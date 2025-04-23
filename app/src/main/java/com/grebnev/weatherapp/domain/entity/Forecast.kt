package com.grebnev.weatherapp.domain.entity

data class Forecast(
    val isDataFromCache: Boolean = false,
    val currentWeather: Weather,
    val upcoming: List<Weather>,
)