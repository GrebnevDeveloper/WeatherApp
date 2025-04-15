package com.grebnev.weatherapp.data.network.api

import com.grebnev.weatherapp.data.network.dto.CityDto
import com.grebnev.weatherapp.data.network.dto.WeatherCurrentDto
import com.grebnev.weatherapp.data.network.dto.WeatherForecastDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface ApiService {
    suspend fun loadWeatherCurrent(query: String): WeatherCurrentDto

    suspend fun loadWeatherForecast(
        query: String,
        daysCount: Int = 4,
    ): WeatherForecastDto

    suspend fun searchCity(query: String): List<CityDto>
}

class ApiServiceImpl(
    private val client: HttpClient,
) : ApiService {
    override suspend fun loadWeatherCurrent(query: String): WeatherCurrentDto =
        client
            .get("current.json") {
                parameter("q", query)
            }.body()

    override suspend fun loadWeatherForecast(
        query: String,
        daysCount: Int,
    ): WeatherForecastDto =
        client
            .get("forecast.json") {
                parameter("q", query)
                parameter("days", daysCount)
            }.body()

    override suspend fun searchCity(query: String): List<CityDto> =
        client
            .get("search.json") { parameter("q", query) }
            .body()
}