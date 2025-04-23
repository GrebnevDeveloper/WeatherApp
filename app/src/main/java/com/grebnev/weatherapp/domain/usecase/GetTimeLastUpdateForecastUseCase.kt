package com.grebnev.weatherapp.domain.usecase

import com.grebnev.weatherapp.domain.repository.WeatherRepository
import com.grebnev.weatherapp.domain.utils.RelevantDataChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class GetTimeLastUpdateForecastUseCase
    @Inject
    constructor(
        private val repository: WeatherRepository,
    ) {
        suspend operator fun invoke(): String {
            val timeLastUpdate =
                withContext(Dispatchers.IO) {
                    repository.getTimeLastUpdateForecast()
                }
            val calendarLastUpdate =
                Calendar.getInstance().apply {
                    time = Date(timeLastUpdate)
                }
            return if (RelevantDataChecker.isWeatherRelevant(timeLastUpdate)) {
                "${calendarLastUpdate.get(Calendar.HOUR_OF_DAY)}:" +
                    "${calendarLastUpdate.get(Calendar.MINUTE)}"
            } else {
                "${calendarLastUpdate.get(Calendar.DAY_OF_MONTH)}." +
                    "${calendarLastUpdate.get(Calendar.MONTH + 1)}." +
                    "${calendarLastUpdate.get(Calendar.YEAR)}"
            }
        }
    }