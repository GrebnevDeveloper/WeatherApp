package com.grebnev.weatherapp.data.mapper

import com.grebnev.weatherapp.data.database.model.ForecastDbModel
import com.grebnev.weatherapp.data.database.model.WeatherDbModel
import com.grebnev.weatherapp.data.network.dto.WeatherCurrentDto
import com.grebnev.weatherapp.data.network.dto.WeatherDto
import com.grebnev.weatherapp.data.network.dto.WeatherForecastDto
import com.grebnev.weatherapp.domain.entity.Forecast
import com.grebnev.weatherapp.domain.entity.Weather
import java.util.Calendar
import java.util.Date

fun WeatherCurrentDto.toWeather(): Weather = weatherCurrent.toWeather()

fun WeatherDto.toWeather(): Weather =
    Weather(
        tempC = tempC,
        conditionText = condition.text,
        conditionUrl = condition.iconUrl.correctionIconUrl(),
        date = date.toCalendar(),
    )

fun WeatherForecastDto.toForecast(): Forecast =
    Forecast(
        currentWeather = weatherCurrent.toWeather(),
        upcoming =
            weatherForecast.forecastDays
                .drop(1) // we skip the first day of weather because this is the current weather
                .map { dayDto ->
                    val dayWeatherDto = dayDto.dayWeather
                    Weather(
                        tempC = dayWeatherDto.tempC,
                        conditionText = dayWeatherDto.condition.text,
                        conditionUrl = dayWeatherDto.condition.iconUrl.correctionIconUrl(),
                        date = dayDto.date.toCalendar(),
                    )
                },
    )

fun WeatherDto.toWeatherDbModel(cityId: Long): WeatherDbModel =
    WeatherDbModel(
        forecastCityId = cityId,
        tempC = tempC,
        conditionText = condition.text,
        conditionUrl = condition.iconUrl.correctionIconUrl(),
        date = date,
    )

fun WeatherForecastDto.toForecastDbModel(cityId: Long): ForecastDbModel =
    ForecastDbModel(
        cityId = cityId,
        currentWeather = weatherCurrent.toWeatherDbModel(cityId),
        upcoming =
            weatherForecast.forecastDays
                .drop(1) // we skip the first day of weather because this is the current weather
                .map { dayDto ->
                    val dayWeatherDto = dayDto.dayWeather
                    WeatherDbModel(
                        forecastCityId = cityId,
                        tempC = dayWeatherDto.tempC,
                        conditionText = dayWeatherDto.condition.text,
                        conditionUrl = dayWeatherDto.condition.iconUrl.correctionIconUrl(),
                        date = dayDto.date,
                    )
                },
    )

fun WeatherDbModel.toWeather(): Weather =
    Weather(
        tempC = tempC,
        conditionText = conditionText,
        conditionUrl = conditionUrl,
        date = date.toCalendar(),
    )

fun ForecastDbModel.toForecast(): Forecast =
    Forecast(
        currentWeather = currentWeather.toWeather(),
        upcoming =
            upcoming
                .map { weatherDbModel ->
                    weatherDbModel.toWeather()
                },
    )

private fun Long.toCalendar() =
    Calendar.getInstance().apply {
        time = Date(this@toCalendar * 1000) // convert to milliseconds
    }

private fun String.correctionIconUrl() =
    "https:$this".replace(
        oldValue = "64x64",
        newValue = "128x128",
    )