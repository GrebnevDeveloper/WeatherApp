package com.grebnev.weatherapp.domain.utils

import java.util.Calendar
import java.util.Date

object RelevantDataChecker {
    fun isWeatherRelevant(timestamp: Long): Boolean {
        val calendarLastUpdate =
            Calendar.getInstance().apply {
                time = Date(timestamp)
            }

        val calendarCurrentTime =
            Calendar.getInstance().apply {
                time = Date(System.currentTimeMillis())
            }

        return calendarLastUpdate.get(Calendar.DAY_OF_MONTH) ==
            calendarCurrentTime.get(Calendar.DAY_OF_MONTH)
    }
}