package com.grebnev.weatherapp.data.database.converter

import com.grebnev.weatherapp.data.database.model.WeatherDbModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class WeatherConverterTest {
    private lateinit var converter: WeatherConverter

    @Before
    fun setUp() {
        converter = WeatherConverter()
    }

    @Test
    fun `fromWeatherDbModel and toWeatherDbModel should convert correctly`() {
        val weather =
            WeatherDbModel(
                id = 1,
                forecastCityId = 2,
                tempC = 25.5f,
                conditionText = "Sunny",
                conditionUrl = "https://example.com/weather.png",
                date = System.currentTimeMillis(),
            )

        val jsonString = converter.fromWeatherDbModel(weather)
        val convertedBack = converter.toWeatherDbModel(jsonString)

        assertNotNull(jsonString)
        assertEquals(weather, convertedBack)
    }

    @Test
    fun `fromWeatherDbModel should return null for null input`() {
        assertNull(converter.fromWeatherDbModel(null))
    }

    @Test
    fun `toWeatherDbModel should return null for null input`() {
        assertNull(converter.toWeatherDbModel(null))
    }

    @Test
    fun `fromWeatherList and toWeatherList should convert list correctly`() {
        val weatherList =
            listOf(
                WeatherDbModel(
                    id = 1,
                    forecastCityId = 2,
                    tempC = 25.5f,
                    conditionText = "Sunny",
                    conditionUrl = "https://example.com/sun.png",
                    date = System.currentTimeMillis(),
                ),
                WeatherDbModel(
                    id = 2,
                    forecastCityId = 2,
                    tempC = 22.0f,
                    conditionText = "Cloudy",
                    conditionUrl = "https://example.com/cloud.png",
                    date = System.currentTimeMillis() + 1000,
                ),
            )

        val jsonString = converter.fromWeatherList(weatherList)
        val convertedBack = converter.toWeatherList(jsonString)

        assertNotNull(jsonString)
        assertEquals(weatherList, convertedBack)
    }

    @Test
    fun `fromWeatherList should return null for null input`() {
        assertNull(converter.fromWeatherList(null))
    }

    @Test
    fun `toWeatherList should return null for null input`() {
        assertNull(converter.toWeatherList(null))
    }

    @Test
    fun `should ignore unknown keys in json`() {
        val jsonWithExtraFields =
            """
            {
                "id": 1,
                "forecastCityId": 2,
                "tempC": 25.5,
                "conditionText": "Sunny",
                "conditionUrl": "https://example.com/sun.png",
                "date": 1234567890,
                "unknownField": "some value"
            }
            """.trimIndent()

        val result = converter.toWeatherDbModel(jsonWithExtraFields)

        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals("Sunny", result?.conditionText)
    }
}