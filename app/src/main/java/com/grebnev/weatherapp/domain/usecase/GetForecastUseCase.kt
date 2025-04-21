package com.grebnev.weatherapp.domain.usecase

import com.grebnev.weatherapp.core.wrappers.ResultStatus
import com.grebnev.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetForecastUseCase
    @Inject
    constructor(
        private val repository: WeatherRepository,
    ) {
        suspend operator fun invoke(cityId: Long) =
            repository.getForecast(cityId).map { result ->
                if (result is ResultStatus.Success) {
                    result.data
                } else {
                    withContext(Dispatchers.IO) {
                        repository.getForecastFromCache(cityId).copy(isDataFromCache = true)
                    }
                }
            }
    }