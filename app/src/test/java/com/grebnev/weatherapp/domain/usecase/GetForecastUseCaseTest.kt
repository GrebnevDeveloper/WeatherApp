package com.grebnev.weatherapp.domain.usecase

import app.cash.turbine.test
import com.grebnev.weatherapp.core.wrappers.ErrorType
import com.grebnev.weatherapp.core.wrappers.OutdatedDataException
import com.grebnev.weatherapp.core.wrappers.ResultStatus
import com.grebnev.weatherapp.domain.entity.Forecast
import com.grebnev.weatherapp.domain.entity.Weather
import com.grebnev.weatherapp.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class GetForecastUseCaseTest {
    private lateinit var useCase: GetForecastUseCase
    private lateinit var repository: WeatherRepository
    private val testCalendar = Calendar.getInstance()

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetForecastUseCase(repository)
    }

    @Test
    fun `invoke should return forecast with isDataFromCache false when API succeeds`() =
        runTest {
            val cityId = 1L
            val testForecast =
                Forecast(
                    currentWeather =
                        Weather(
                            tempC = 25.5f,
                            conditionText = "Sunny",
                            conditionUrl = "sunny.url",
                            date = testCalendar,
                        ),
                    upcoming =
                        listOf(
                            Weather(
                                tempC = 22.0f,
                                conditionText = "Cloudy",
                                conditionUrl = "cloudy.url",
                                date = testCalendar,
                            ),
                        ),
                    isDataFromCache = false,
                )
            coEvery { repository.getForecast(cityId) } returns flowOf(ResultStatus.Success(testForecast))

            useCase(cityId).test {
                val result = awaitItem()
                assertEquals(25.5f, result.currentWeather.tempC)
                assertEquals(1, result.upcoming.size)
                assertEquals("Cloudy", result.upcoming[0].conditionText)
                assertFalse(result.isDataFromCache)
                awaitComplete()
            }
        }

    @Test
    fun `invoke should return forecast with isDataFromCache true when API fails but cache is relevant`() =
        runTest {
            val cityId = 1L
            val cachedForecast =
                Forecast(
                    currentWeather =
                        Weather(
                            tempC = 23.0f,
                            conditionText = "Partly Cloudy",
                            conditionUrl = "partly_cloudy.url",
                            date = testCalendar,
                        ),
                    upcoming = emptyList(),
                    isDataFromCache = false,
                )
            val lastUpdateTime = System.currentTimeMillis() - 1000 * 60 * 30

            coEvery { repository.getForecast(cityId) } returns
                flowOf(ResultStatus.Error(ErrorType.NETWORK_ERROR))
            coEvery { repository.getTimeLastUpdateForecast() } returns lastUpdateTime
            coEvery { repository.getForecastFromCache(cityId) } returns cachedForecast

            useCase(cityId).test {
                val result = awaitItem()
                assertEquals(23.0f, result.currentWeather.tempC)
                assertTrue(result.upcoming.isEmpty())
                assertTrue(result.isDataFromCache)
                assertEquals("Partly Cloudy", result.currentWeather.conditionText)
                awaitComplete()
            }
        }

    @Test
    fun `invoke should throw when API fails and cache is outdated`() =
        runTest {
            val cityId = 1L
            val lastUpdateTime = System.currentTimeMillis() - 1000 * 60 * 1200

            coEvery { repository.getForecast(cityId) } returns
                flowOf(ResultStatus.Error(ErrorType.NETWORK_ERROR))
            coEvery { repository.getTimeLastUpdateForecast() } returns lastUpdateTime

            useCase(cityId).test {
                val error = awaitError()
                assertTrue(error is OutdatedDataException)
            }
        }

    @Test
    fun `invoke should verify repository calls for successful API request`() =
        runTest {
            val cityId = 1L
            val testForecast = createTestForecast()
            coEvery { repository.getForecast(cityId) } returns flowOf(ResultStatus.Success(testForecast))

            useCase(cityId).test {
                awaitItem()
                awaitComplete()
            }

            coVerify(exactly = 1) { repository.getForecast(cityId) }
            coVerify(exactly = 0) { repository.getTimeLastUpdateForecast() }
            coVerify(exactly = 0) { repository.getForecastFromCache(any()) }
        }

    @Test
    fun `cached forecast should have isDataFromCache set to true`() =
        runTest {
            val cityId = 1L
            val cachedForecast = createTestForecast(isDataFromCache = false)
            val lastUpdateTime = System.currentTimeMillis() - 1000 * 60 * 30

            coEvery { repository.getForecast(cityId) } returns
                flowOf(ResultStatus.Error(ErrorType.NETWORK_ERROR))
            coEvery { repository.getTimeLastUpdateForecast() } returns lastUpdateTime
            coEvery { repository.getForecastFromCache(cityId) } returns cachedForecast

            useCase(cityId).test {
                val result = awaitItem()
                assertTrue(result.isDataFromCache)
                assertFalse(result.currentWeather.isDataFromCache)
                assertEquals(2, result.upcoming.size)
                awaitComplete()
            }
        }

    private fun createTestForecast(isDataFromCache: Boolean = false): Forecast {
        val calendar = Calendar.getInstance()
        return Forecast(
            currentWeather =
                Weather(
                    tempC = 25.5f,
                    conditionText = "Sunny",
                    conditionUrl = "sunny.url",
                    date = calendar,
                    isDataFromCache = isDataFromCache,
                ),
            upcoming =
                listOf(
                    Weather(
                        tempC = 22.0f,
                        conditionText = "Cloudy",
                        conditionUrl = "cloudy.url",
                        date = calendar,
                    ),
                    Weather(
                        tempC = 18.0f,
                        conditionText = "Rainy",
                        conditionUrl = "rainy.url",
                        date = calendar,
                    ),
                ),
            isDataFromCache = isDataFromCache,
        )
    }
}