package com.grebnev.weatherapp.domain.repository

import com.grebnev.weatherapp.domain.entity.Forecast
import com.grebnev.weatherapp.domain.entity.Weather

interface WeatherRepository {
    suspend fun getWeather(cityId: Long): Weather

    suspend fun getForecast(cityId: Long): Forecast

    suspend fun getWeatherFromCache(cityId: Long): Weather

    suspend fun getForecastFromCache(cityId: Long): Forecast
}