package com.grebnev.weatherapp.domain.usecase

import app.cash.turbine.test
import com.grebnev.weatherapp.core.wrappers.ErrorType
import com.grebnev.weatherapp.core.wrappers.OutdatedDataException
import com.grebnev.weatherapp.core.wrappers.ResultStatus
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

class GetCurrentWeatherUseCaseTest {
    private lateinit var useCase: GetCurrentWeatherUseCase
    private lateinit var repository: WeatherRepository
    private val testCalendar = Calendar.getInstance()

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetCurrentWeatherUseCase(repository)
    }

    @Test
    fun `invoke should return weather with isDataFromCache false when API succeeds`() =
        runTest {
            val cityId = 1L
            val testWeather =
                Weather(
                    tempC = 25.5f,
                    conditionText = "Sunny",
                    conditionUrl = "test.url",
                    date = testCalendar,
                    isDataFromCache = false,
                )
            coEvery { repository.getWeather(cityId) } returns flowOf(ResultStatus.Success(testWeather))

            useCase(cityId).test {
                val result = awaitItem()
                assertEquals(25.5f, result.tempC)
                assertEquals("Sunny", result.conditionText)
                assertEquals("test.url", result.conditionUrl)
                assertEquals(testCalendar, result.date)
                assertFalse(result.isDataFromCache)
                awaitComplete()
            }
        }

    @Test
    fun `invoke should return weather with isDataFromCache true when API fails but cache is relevant`() =
        runTest {
            // Arrange
            val cityId = 1L
            val cachedWeather =
                Weather(
                    tempC = 22.0f,
                    conditionText = "Cloudy",
                    conditionUrl = "cached.url",
                    date = testCalendar,
                    isDataFromCache = false,
                )
            val lastUpdateTime = System.currentTimeMillis() - 1000 * 60 * 30

            coEvery { repository.getWeather(cityId) } returns
                flowOf(ResultStatus.Error(ErrorType.NETWORK_ERROR))
            coEvery { repository.getTimeLastUpdateForecast() } returns lastUpdateTime
            coEvery { repository.getWeatherFromCache(cityId) } returns cachedWeather

            useCase(cityId).test {
                val result = awaitItem()
                assertEquals(22.0f, result.tempC)
                assertEquals("Cloudy", result.conditionText)
                assertEquals("cached.url", result.conditionUrl)
                assertEquals(testCalendar, result.date)
                assertTrue(result.isDataFromCache)
                awaitComplete()
            }
        }

    @Test
    fun `invoke should throw when API fails and cache is outdated`() =
        runTest {
            val cityId = 1L
            val lastUpdateTime = System.currentTimeMillis() - 1000 * 60 * 1200

            coEvery { repository.getWeather(cityId) } returns
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
            val testWeather =
                Weather(
                    tempC = 25.5f,
                    conditionText = "Sunny",
                    conditionUrl = "test.url",
                    date = testCalendar,
                )
            coEvery { repository.getWeather(cityId) } returns flowOf(ResultStatus.Success(testWeather))

            useCase(cityId).test {
                awaitItem()
                awaitComplete()
            }

            coVerify(exactly = 1) { repository.getWeather(cityId) }
            coVerify(exactly = 0) { repository.getTimeLastUpdateForecast() }
            coVerify(exactly = 0) { repository.getWeatherFromCache(any()) }
        }

    @Test
    fun `cached weather should have isDataFromCache set to true`() =
        runTest {
            val cityId = 1L
            val cachedWeather =
                Weather(
                    tempC = 22.0f,
                    conditionText = "Cloudy",
                    conditionUrl = "cached.url",
                    date = testCalendar,
                    isDataFromCache = false,
                )
            val lastUpdateTime = System.currentTimeMillis() - 1000 * 60 * 30

            coEvery { repository.getWeather(cityId) } returns
                flowOf(ResultStatus.Error(ErrorType.NETWORK_ERROR))
            coEvery { repository.getTimeLastUpdateForecast() } returns lastUpdateTime
            coEvery { repository.getWeatherFromCache(cityId) } returns cachedWeather

            useCase(cityId).test {
                val result = awaitItem()
                assertTrue(result.isDataFromCache)
                assertEquals(22.0f, result.tempC)
                awaitComplete()
            }
        }
}