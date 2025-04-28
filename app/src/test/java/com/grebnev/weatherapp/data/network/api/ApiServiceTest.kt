package com.grebnev.weatherapp.data.network.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ApiServiceTest {
    private fun createMockClient(): HttpClient =
        HttpClient(
            MockEngine.create {
                addHandler { request ->
                    when (request.url.encodedPath) {
                        "/current.json" ->
                            respond(
                                content =
                                    ByteReadChannel(
                                        """
                                        {
                                            "current": {
                                                "last_updated_epoch": 123456789,
                                                "temp_c": 25.5,
                                                "condition": {
                                                    "text": "Sunny",
                                                    "icon": "//cdn.weatherapi.com/weather/64x64/day/113.png"
                                                }
                                            }
                                        }
                                        """.trimIndent(),
                                    ),
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json"),
                            )

                        "/forecast.json" ->
                            respond(
                                content =
                                    ByteReadChannel(
                                        """
                                        {
                                            "current": {
                                                "last_updated_epoch": 123456789,
                                                "temp_c": 25.5,
                                                "condition": {
                                                    "text": "Sunny",
                                                    "icon": "//cdn.weatherapi.com/weather/64x64/day/113.png"
                                                }
                                            },
                                            "forecast": {
                                                "forecastday": [
                                                    {
                                                        "date_epoch": 123456789,
                                                        "day": {
                                                            "avgtemp_c": 22.3,
                                                            "condition": {
                                                                "text": "Partly cloudy",
                                                                "icon": "//cdn.weatherapi.com/weather/64x64/day/116.png"
                                                            }
                                                        }
                                                    }
                                                ]
                                            }
                                        }
                                        """.trimIndent(),
                                    ),
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json"),
                            )

                        "/search.json" ->
                            respond(
                                content =
                                    ByteReadChannel(
                                        """
                                        [
                                            {
                                                "id": 1,
                                                "name": "Moscow",
                                                "country": "Russia"
                                            },
                                            {
                                                "id": 2,
                                                "name": "Mostar",
                                                "country": "Bosnia and Herzegovina"
                                            }
                                        ]
                                        """.trimIndent(),
                                    ),
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json"),
                            )

                        else -> error("Unhandled path: ${request.url.encodedPath}")
                    }
                }
            },
        ) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
        }

    @Test
    fun `loadWeatherCurrent should return weather data`() =
        runTest {
            val apiService = ApiServiceImpl(createMockClient())
            val result = apiService.loadWeatherCurrent("Moscow")

            assertEquals(123456789L, result.weatherCurrent.date)
            assertEquals(25.5f, result.weatherCurrent.tempC)
            assertEquals("Sunny", result.weatherCurrent.condition.text)
        }

    @Test
    fun `loadWeatherForecast should return forecast data`() =
        runTest {
            val apiService = ApiServiceImpl(createMockClient())
            val result = apiService.loadWeatherForecast("Moscow", 4)

            assertEquals(1, result.weatherForecast.forecastDays.size)
            assertEquals(123456789L, result.weatherForecast.forecastDays[0].date)
            assertEquals(
                22.3f,
                result.weatherForecast.forecastDays[0]
                    .dayWeather.tempC,
            )
        }

    @Test
    fun `searchCity should return list of cities`() =
        runTest {
            val apiService = ApiServiceImpl(createMockClient())
            val result = apiService.searchCity("Mos")

            assertEquals(2, result.size)
            assertEquals("Moscow", result[0].name)
            assertEquals("Russia", result[0].country)
        }

    @Test
    fun `should throw exception for unhandled paths`() =
        runTest {
            val client =
                HttpClient(
                    MockEngine.create {
                        addHandler { _ ->
                            error("Test error")
                        }
                    },
                )

            val apiService = ApiServiceImpl(client)

            assertFailsWith<Exception> {
                apiService.loadWeatherCurrent("Unknown")
            }
        }
}