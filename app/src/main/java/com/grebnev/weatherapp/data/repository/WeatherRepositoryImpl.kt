package com.grebnev.weatherapp.data.repository

import com.grebnev.weatherapp.data.database.dao.ForecastDao
import com.grebnev.weatherapp.data.mapper.toForecast
import com.grebnev.weatherapp.data.mapper.toWeather
import com.grebnev.weatherapp.data.network.api.ApiService
import com.grebnev.weatherapp.domain.entity.Forecast
import com.grebnev.weatherapp.domain.entity.Weather
import com.grebnev.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl
    @Inject
    constructor(
        private val apiService: ApiService,
        private val forecastDao: ForecastDao,
    ) : WeatherRepository {
        override suspend fun getWeather(cityId: Long) =
            apiService.loadWeatherCurrent("$PREFIX_CITY_ID$cityId").toWeather()

        override suspend fun getForecast(cityId: Long) =
            apiService.loadWeatherForecast("$PREFIX_CITY_ID$cityId").toForecast()

        override suspend fun getWeatherFromCache(cityId: Long): Weather =
            forecastDao.getCurrentWeatherForCity(cityId).toWeather()

        override suspend fun getForecastFromCache(cityId: Long): Forecast =
            forecastDao.getForecastForCity(cityId).toForecast()

        companion object {
            private const val PREFIX_CITY_ID = "id:"
        }
    }