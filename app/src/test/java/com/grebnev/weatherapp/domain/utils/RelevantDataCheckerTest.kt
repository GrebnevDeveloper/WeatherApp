package com.grebnev.weatherapp.domain.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class RelevantDataCheckerTest {
    @Test
    fun `isWeatherRelevant should return true when same day`() {
        val currentTime = System.currentTimeMillis()

        val result = RelevantDataChecker.isWeatherRelevant(currentTime)

        assertTrue(result)
    }

    @Test
    fun `isWeatherRelevant should return false when different day`() {
        val calendar =
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, -1)
            }
        val yesterdayTime = calendar.timeInMillis

        val result = RelevantDataChecker.isWeatherRelevant(yesterdayTime)

        assertFalse(result)
    }

    @Test
    fun `isWeatherRelevant should return true when same day different time`() {
        val calendar =
            Calendar.getInstance().apply {
                add(Calendar.HOUR_OF_DAY, -3)
            }
        val earlierTodayTime = calendar.timeInMillis

        val result = RelevantDataChecker.isWeatherRelevant(earlierTodayTime)

        assertTrue(result)
    }

    @Test
    fun `isWeatherRelevant should handle month boundary correctly`() {
        val calendar =
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.DAY_OF_MONTH, -1)
            }
        val lastDayOfPrevMonth = calendar.timeInMillis

        val result = RelevantDataChecker.isWeatherRelevant(lastDayOfPrevMonth)

        assertFalse(result)
    }

    @Test
    fun `isWeatherRelevant should handle year boundary correctly`() {
        val calendar =
            Calendar.getInstance().apply {
                set(Calendar.MONTH, Calendar.JANUARY)
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.DAY_OF_MONTH, -1)
            }
        val lastDayOfYear = calendar.timeInMillis

        val result = RelevantDataChecker.isWeatherRelevant(lastDayOfYear)

        assertFalse(result)
    }
}