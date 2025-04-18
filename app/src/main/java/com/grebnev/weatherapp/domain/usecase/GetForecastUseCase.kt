package com.grebnev.weatherapp.domain.usecase

import com.grebnev.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetForecastUseCase
    @Inject
    constructor(
        private val repository: WeatherRepository,
    ) {
        suspend operator fun invoke(cityId: Long) = repository.getForecast(cityId)

        suspend fun fromCache(cityId: Long) = repository.getForecastFromCache(cityId)
    }