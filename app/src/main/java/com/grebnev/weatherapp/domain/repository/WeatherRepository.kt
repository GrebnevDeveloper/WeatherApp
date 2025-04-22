package com.grebnev.weatherapp.domain.repository

import com.grebnev.weatherapp.core.wrappers.ErrorType
import com.grebnev.weatherapp.core.wrappers.ResultStatus
import com.grebnev.weatherapp.domain.entity.Forecast
import com.grebnev.weatherapp.domain.entity.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeather(cityId: Long): Flow<ResultStatus<Weather, ErrorType>>

    suspend fun getForecast(cityId: Long): Flow<ResultStatus<Forecast, ErrorType>>

    suspend fun getWeatherFromCache(cityId: Long): Weather

    suspend fun getForecastFromCache(cityId: Long): Forecast

    suspend fun getTimeLastUpdateForecast(): Long
}