package com.grebnev.weatherapp.presentation.extensions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarExtTest {
    @Before
    fun setUp() {
        Locale.setDefault(Locale.ENGLISH)
    }

    @Test
    fun `formattedFullDate should return correct format`() {
        val calendar =
            Calendar.getInstance().apply {
                set(2023, Calendar.JUNE, 15)
            }

        val expectedFormat =
            SimpleDateFormat("EEEE | d MMM y", Locale.getDefault())
                .format(calendar.time)

        val result = calendar.formattedFullDate()

        assertEquals(expectedFormat, result)
        assertTrue(result.contains("Jun"))
        assertTrue(result.contains("2023"))
    }

    @Test
    fun `formattedShortDayOfWeek should return 3-letter abbreviation`() {
        val calendar =
            Calendar.getInstance().apply {
                set(2023, Calendar.JUNE, 15)
            }
        val expectedFormat =
            SimpleDateFormat("EEE", Locale.getDefault())
                .format(calendar.time)

        val result = calendar.formattedShortDayOfWeek()

        assertEquals(expectedFormat, result)
        assertEquals(3, result.length)
    }

    @Test
    fun `formatted functions should respect locale`() {
        val calendar =
            Calendar.getInstance().apply {
                set(2023, Calendar.JUNE, 15)
            }
        val originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.FRENCH)

        try {
            val fullDate = calendar.formattedFullDate()
            val shortDay = calendar.formattedShortDayOfWeek()

            assertTrue(fullDate.contains("juin"))
            assertEquals("jeu.", shortDay)
        } finally {
            Locale.setDefault(originalLocale)
        }
    }
}