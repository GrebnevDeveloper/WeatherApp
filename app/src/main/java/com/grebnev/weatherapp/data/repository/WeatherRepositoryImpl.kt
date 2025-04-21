package com.grebnev.weatherapp.data.repository

import com.grebnev.weatherapp.core.handlers.ErrorHandler
import com.grebnev.weatherapp.core.wrappers.ErrorType
import com.grebnev.weatherapp.core.wrappers.ResultStatus
import com.grebnev.weatherapp.data.database.dao.ForecastDao
import com.grebnev.weatherapp.data.mapper.toForecast
import com.grebnev.weatherapp.data.mapper.toWeather
import com.grebnev.weatherapp.data.network.api.ApiService
import com.grebnev.weatherapp.domain.entity.Forecast
import com.grebnev.weatherapp.domain.entity.Weather
import com.grebnev.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import timber.log.Timber
import javax.inject.Inject

class WeatherRepositoryImpl
    @Inject
    constructor(
        private val apiService: ApiService,
        private val forecastDao: ForecastDao,
    ) : WeatherRepository {
        override suspend fun getWeather(cityId: Long) =
            flow {
                val weather = apiService.loadWeatherCurrent("$PREFIX_CITY_ID$cityId").toWeather()
                emit(ResultStatus.Success(weather) as ResultStatus<Weather, ErrorType>)
            }.retry(ErrorHandler.MAX_COUNT_RETRY) { throwable ->
                delay(ErrorHandler.RETRY_TIMEOUT)
                true
            }.catch { exception ->
                Timber.e(exception, "Error loading the weather")
                val errorType = ErrorHandler.getErrorTypeByError(exception)
                emit(ResultStatus.Error(errorType))
            }

        override suspend fun getForecast(cityId: Long) =
            flow {
                val forecast = apiService.loadWeatherForecast("$PREFIX_CITY_ID$cityId").toForecast()
                emit(ResultStatus.Success(forecast) as ResultStatus<Forecast, ErrorType>)
            }.retry(ErrorHandler.MAX_COUNT_RETRY) {
                delay(ErrorHandler.RETRY_TIMEOUT)
                true
            }.catch { exception ->
                Timber.e(exception, "Error loading the forecast")
                val errorType = ErrorHandler.getErrorTypeByError(exception)
                emit(ResultStatus.Error(errorType))
            }

        override suspend fun getWeatherFromCache(cityId: Long): Weather =
            forecastDao.getCurrentWeatherForCity(cityId).toWeather()

        override suspend fun getForecastFromCache(cityId: Long): Forecast =
            forecastDao.getForecastForCity(cityId).toForecast()

        companion object {
            private const val PREFIX_CITY_ID = "id:"
        }
    }