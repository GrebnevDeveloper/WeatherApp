package com.grebnev.weatherapp.presentation.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {
    @Test
    fun `toTempCString should round and add degree symbol`() {
        val testCases =
            listOf(
                23.4f to "23°C",
                23.5f to "24°C",
                -5.9f to "-6°C",
                0.0f to "0°C",
                17.49f to "17°C",
            )

        testCases.forEach { (input, expected) ->
            val result = input.toTempCString()

            assertEquals(expected, result)
        }
    }
}