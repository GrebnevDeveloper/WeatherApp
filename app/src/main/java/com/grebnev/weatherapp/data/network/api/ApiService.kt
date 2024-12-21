package com.grebnev.weatherapp.data.network.api

import com.grebnev.weatherapp.data.network.dto.CityDto
import com.grebnev.weatherapp.data.network.dto.WeatherCurrentDto
import com.grebnev.weatherapp.data.network.dto.WeatherForecastDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("current.json")
    suspend fun loadWeatherCurrent(
        @Query("q") query: String
    ): WeatherCurrentDto

    @GET("forecast.json")
    suspend fun loadWeatherForecast(
        @Query("q") query: String,
        @Query("days") daysCount: Int = 4
    ): WeatherForecastDto

    @GET("search.json")
    suspend fun searchCity(
        @Query("q") query: String
    ): List<CityDto>
}