package com.grebnev.weatherapp.data.repository

import com.grebnev.weatherapp.data.mapper.toForecast
import com.grebnev.weatherapp.data.mapper.toWeather
import com.grebnev.weatherapp.data.network.api.ApiService
import com.grebnev.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : WeatherRepository {
    override suspend fun getWeather(cityId: Long) =
        apiService.loadWeatherCurrent("$PREFIX_CITY_ID$cityId").toWeather()

    override suspend fun getForecast(cityId: Long) =
        apiService.loadWeatherForecast("$PREFIX_CITY_ID$cityId").toForecast()

    companion object {
        private const val PREFIX_CITY_ID = "id:"
    }
}