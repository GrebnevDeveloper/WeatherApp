package com.grebnev.weatherapp.domain.usecase

import com.grebnev.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            return timeFormat.format(Date(timeLastUpdate))
        }
    }