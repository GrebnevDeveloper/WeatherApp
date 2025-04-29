package com.grebnev.weatherapp.data.repository

import app.cash.turbine.test
import com.grebnev.weatherapp.core.wrappers.OutdatedDataException
import com.grebnev.weatherapp.core.wrappers.ResultStatus
import com.grebnev.weatherapp.data.database.dao.ForecastDao
import com.grebnev.weatherapp.data.database.dao.MetadataDao
import com.grebnev.weatherapp.data.database.model.ForecastDbModel
import com.grebnev.weatherapp.data.database.model.WeatherDbModel
import com.grebnev.weatherapp.data.network.api.ApiService
import com.grebnev.weatherapp.data.network.dto.ConditionDto
import com.grebnev.weatherapp.data.network.dto.DayDto
import com.grebnev.weatherapp.data.network.dto.DayWeatherDto
import com.grebnev.weatherapp.data.network.dto.ForecastDayDto
import com.grebnev.weatherapp.data.network.dto.WeatherCurrentDto
import com.grebnev.weatherapp.data.network.dto.WeatherDto
import com.grebnev.weatherapp.data.network.dto.WeatherForecastDto
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class WeatherRepositoryImplTest {
    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var forecastDao: ForecastDao

    @MockK
    private lateinit var metadataDao: MetadataDao

    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = WeatherRepositoryImpl(apiService, forecastDao, metadataDao)
    }

    @Test
    fun `getWeather should emit Success with weather data`() =
        runTest {
            val cityId = 1L
            val weatherDto =
                WeatherCurrentDto(
                    weatherCurrent =
                        WeatherDto(
                            date = 123456789L,
                            tempC = 25.5f,
                            condition = ConditionDto("Sunny", "//test.com/icon.png"),
                        ),
                )
            coEvery { apiService.loadWeatherCurrent("id:1") } returns weatherDto

            repository.getWeather(cityId).test {
                val result = awaitItem()
                assertTrue(result is ResultStatus.Success)
                assertEquals(25.5f, (result as ResultStatus.Success).data.tempC)
                assertEquals("Sunny", result.data.conditionText)
                awaitComplete()
            }
        }

    @Test
    fun `getWeather should retry on failure and then emit Error`() =
        runTest {
            val cityId = 1L
            coEvery { apiService.loadWeatherCurrent("id:1") } throws IOException("Network error")

            repository.getWeather(cityId).test {
                val result = awaitItem()
                assertTrue(result is ResultStatus.Error)
                awaitComplete()
            }

            coVerify(exactly = 4) {
                apiService.loadWeatherCurrent("id:1")
            }
        }

    @Test
    fun `getForecast should emit Success with forecast data`() =
        runTest {
            val cityId = 1L
            val forecastDto =
                WeatherForecastDto(
                    weatherCurrent =
                        WeatherDto(
                            date = 123456789L,
                            tempC = 25.5f,
                            condition = ConditionDto("Sunny", "//test.com/icon.png"),
                        ),
                    weatherForecast =
                        ForecastDayDto(
                            forecastDays =
                                listOf(
                                    DayDto(
                                        date = 123456789L,
                                        dayWeather =
                                            DayWeatherDto(
                                                tempC = 22.0f,
                                                condition = ConditionDto("Cloudy", "//test.com/icon2.png"),
                                            ),
                                    ),
                                ),
                        ),
                )
            coEvery { apiService.loadWeatherForecast("id:1") } returns forecastDto

            repository.getForecast(cityId).test {
                val result = awaitItem()
                assertTrue(result is ResultStatus.Success)
                val forecast = (result as ResultStatus.Success).data
                assertEquals(25.5f, forecast.currentWeather.tempC)
                assertEquals(0, forecast.upcoming.size)
                awaitComplete()
            }
        }

    @Test
    fun `getWeatherFromCache should return weather when exists`() =
        runTest {
            val cityId = 1L
            val dbModel =
                WeatherDbModel(
                    forecastCityId = cityId,
                    tempC = 20.0f,
                    conditionText = "Cloudy",
                    conditionUrl = "//test.com/icon.png",
                    date = 123456789L,
                )
            coEvery { forecastDao.getCurrentWeatherForCity(cityId) } returns dbModel

            val result = repository.getWeatherFromCache(cityId)

            assertEquals(20.0f, result.tempC)
            assertEquals("Cloudy", result.conditionText)
        }

    @Test(expected = OutdatedDataException::class)
    fun `getWeatherFromCache should throw when no data`() =
        runTest {
            val cityId = 1L
            coEvery { forecastDao.getCurrentWeatherForCity(cityId) } returns null

            repository.getWeatherFromCache(cityId)
        }

    @Test
    fun `getForecastFromCache should return forecast when exists`() =
        runTest {
            val cityId = 1L
            val dbModel =
                ForecastDbModel(
                    cityId = cityId,
                    currentWeather =
                        WeatherDbModel(
                            forecastCityId = cityId,
                            tempC = 20.0f,
                            conditionText = "Cloudy",
                            conditionUrl = "//test.com/icon.png",
                            date = 123456789L,
                        ),
                    upcoming = emptyList(),
                )
            coEvery { forecastDao.getForecastForCity(cityId) } returns dbModel

            val result = repository.getForecastFromCache(cityId)

            assertEquals(20.0f, result.currentWeather.tempC)
            assertTrue(result.upcoming.isEmpty())
        }

    @Test(expected = OutdatedDataException::class)
    fun `getForecastFromCache should throw when no data`() =
        runTest {
            val cityId = 1L
            coEvery { forecastDao.getForecastForCity(cityId) } returns null

            repository.getForecastFromCache(cityId)
        }

    @Test
    fun `getTimeLastUpdateForecast should return time when exists`() =
        runTest {
            val expectedTime = 123456789L
            coEvery { metadataDao.getTimeLastUpdateForecast() } returns expectedTime

            val result = repository.getTimeLastUpdateForecast()

            assertEquals(expectedTime, result)
        }

    @Test(expected = OutdatedDataException::class)
    fun `getTimeLastUpdateForecast should throw when no data`() =
        runTest {
            coEvery { metadataDao.getTimeLastUpdateForecast() } returns null

            repository.getTimeLastUpdateForecast()
        }
}