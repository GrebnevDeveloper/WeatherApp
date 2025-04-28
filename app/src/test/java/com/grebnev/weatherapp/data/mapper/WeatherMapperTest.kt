package com.grebnev.weatherapp.data.mapper

import com.grebnev.weatherapp.data.database.model.ForecastDbModel
import com.grebnev.weatherapp.data.database.model.WeatherDbModel
import com.grebnev.weatherapp.data.network.dto.ConditionDto
import com.grebnev.weatherapp.data.network.dto.DayDto
import com.grebnev.weatherapp.data.network.dto.DayWeatherDto
import com.grebnev.weatherapp.data.network.dto.ForecastDayDto
import com.grebnev.weatherapp.data.network.dto.WeatherCurrentDto
import com.grebnev.weatherapp.data.network.dto.WeatherDto
import com.grebnev.weatherapp.data.network.dto.WeatherForecastDto
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class WeatherMapperTest {
    private val testTimestamp = 123456789L
    private val testCityId = 1L
    private val originalIconUrl = "//cdn.weatherapi.com/weather/64x64/day/113.png"
    private val correctedIconUrl = "https://cdn.weatherapi.com/weather/128x128/day/113.png"

    private lateinit var baseWeatherDto: WeatherDto

    @Before
    fun setUp() {
        baseWeatherDto =
            WeatherDto(
                date = testTimestamp,
                tempC = 25.5f,
                condition = ConditionDto("Sunny", originalIconUrl),
            )
    }

    @Test
    fun `WeatherDto to Weather conversion checks all fields`() {
        val weather = baseWeatherDto.toWeather()

        assertEquals(baseWeatherDto.tempC, weather.tempC)
        assertEquals(baseWeatherDto.condition.text, weather.conditionText)
        assertEquals(correctedIconUrl, weather.conditionUrl)
        assertEquals(Date(testTimestamp * 1000), weather.date.time)
    }

    @Test
    fun `WeatherCurrentDto to Weather conversion delegates to WeatherDto`() {
        val currentDto = WeatherCurrentDto(baseWeatherDto)
        val weather = currentDto.toWeather()

        assertEquals(baseWeatherDto.tempC, weather.tempC)
        assertEquals(baseWeatherDto.condition.text, weather.conditionText)
    }

    @Test
    fun `WeatherForecastDto to Forecast conversion checks structure and transformations`() {
        val forecastDto =
            WeatherForecastDto(
                weatherCurrent = baseWeatherDto,
                weatherForecast =
                    ForecastDayDto(
                        forecastDays =
                            listOf(
                                DayDto(
                                    date = testTimestamp,
                                    dayWeather =
                                        DayWeatherDto(
                                            tempC = 22.3f,
                                            condition = ConditionDto("Cloudy", originalIconUrl),
                                        ),
                                ),
                                DayDto(
                                    date = testTimestamp + 1,
                                    dayWeather =
                                        DayWeatherDto(
                                            tempC = 20.0f,
                                            condition = ConditionDto("Rainy", originalIconUrl),
                                        ),
                                ),
                            ),
                    ),
            )

        val forecast = forecastDto.toForecast()

        // Проверка текущей погоды
        assertEquals(baseWeatherDto.tempC, forecast.currentWeather.tempC)
        assertEquals(correctedIconUrl, forecast.currentWeather.conditionUrl)

        // Проверка прогноза (пропуск первого дня)
        assertEquals(1, forecast.upcoming.size)
        assertEquals(20.0f, forecast.upcoming[0].tempC)
        assertEquals(correctedIconUrl, forecast.upcoming[0].conditionUrl)
    }

    @Test
    fun `WeatherDto to WeatherDbModel conversion checks all fields`() {
        val dbModel = baseWeatherDto.toWeatherDbModel(testCityId)

        assertEquals(testCityId, dbModel.forecastCityId)
        assertEquals(baseWeatherDto.tempC, dbModel.tempC)
        assertEquals(baseWeatherDto.condition.text, dbModel.conditionText)
        assertEquals(correctedIconUrl, dbModel.conditionUrl)
        assertEquals(baseWeatherDto.date, dbModel.date)
    }

    @Test
    fun `WeatherForecastDto to ForecastDbModel conversion checks structure`() {
        val forecastDto =
            WeatherForecastDto(
                weatherCurrent = baseWeatherDto,
                weatherForecast =
                    ForecastDayDto(
                        forecastDays =
                            listOf(
                                DayDto(
                                    date = testTimestamp,
                                    dayWeather =
                                        DayWeatherDto(
                                            tempC = 22.3f,
                                            condition = ConditionDto("Cloudy", originalIconUrl),
                                        ),
                                ),
                                DayDto(
                                    date = testTimestamp + 1,
                                    dayWeather =
                                        DayWeatherDto(
                                            tempC = 20.0f,
                                            condition = ConditionDto("Rainy", originalIconUrl),
                                        ),
                                ),
                            ),
                    ),
            )

        val dbModel = forecastDto.toForecastDbModel(testCityId)

        assertEquals(testCityId, dbModel.cityId)
        assertEquals(1, dbModel.upcoming.size) // Первый день пропускается
        assertEquals(baseWeatherDto.tempC, dbModel.currentWeather.tempC)
        assertEquals(correctedIconUrl, dbModel.currentWeather.conditionUrl)
    }

    @Test
    fun `WeatherDbModel to Weather conversion checks all fields`() {
        val dbModel =
            WeatherDbModel(
                forecastCityId = testCityId,
                tempC = 25.5f,
                conditionText = "Sunny",
                conditionUrl = correctedIconUrl,
                date = testTimestamp,
            )

        val weather = dbModel.toWeather()

        assertEquals(dbModel.tempC, weather.tempC)
        assertEquals(dbModel.conditionText, weather.conditionText)
        assertEquals(dbModel.conditionUrl, weather.conditionUrl)
        assertEquals(Date(dbModel.date * 1000), weather.date.time)
    }

    @Test
    fun `ForecastDbModel to Forecast conversion checks structure`() {
        val dbModel =
            ForecastDbModel(
                cityId = testCityId,
                currentWeather =
                    WeatherDbModel(
                        forecastCityId = testCityId,
                        tempC = 25.5f,
                        conditionText = "Sunny",
                        conditionUrl = correctedIconUrl,
                        date = testTimestamp,
                    ),
                upcoming =
                    listOf(
                        WeatherDbModel(
                            forecastCityId = testCityId,
                            tempC = 22.0f,
                            conditionText = "Cloudy",
                            conditionUrl = correctedIconUrl,
                            date = testTimestamp + 1,
                        ),
                    ),
            )

        val forecast = dbModel.toForecast()

        assertEquals(25.5f, forecast.currentWeather.tempC)
        assertEquals(1, forecast.upcoming.size)
        assertEquals(22.0f, forecast.upcoming[0].tempC)
        assertEquals(correctedIconUrl, forecast.upcoming[0].conditionUrl)
    }
}