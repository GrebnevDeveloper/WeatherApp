package com.grebnev.weatherapp.domain.usecase

import com.grebnev.weatherapp.core.wrappers.OutdatedDataException
import com.grebnev.weatherapp.core.wrappers.ResultStatus
import com.grebnev.weatherapp.domain.repository.WeatherRepository
import com.grebnev.weatherapp.domain.utils.RelevantDataChecker
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCurrentWeatherUseCase
    @Inject
    constructor(
        private val repository: WeatherRepository,
    ) {
        suspend operator fun invoke(cityId: Long) =
            repository.getWeather(cityId).map { result ->
                if (result is ResultStatus.Success) {
                    result.data
                } else {
                    val timeLastUpdate = repository.getTimeLastUpdateForecast()
                    if (RelevantDataChecker.isWeatherRelevant(timeLastUpdate)) {
                        repository.getWeatherFromCache(cityId).copy(isDataFromCache = true)
                    } else {
                        throw OutdatedDataException("The current weather is outdated")
                    }
                }
            }
    }