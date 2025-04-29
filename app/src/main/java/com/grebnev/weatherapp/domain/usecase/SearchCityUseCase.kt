package com.grebnev.weatherapp.domain.usecase

import com.grebnev.weatherapp.domain.repository.SearchRepository
import javax.inject.Inject

class SearchCityUseCase @Inject constructor(
    private val repository: SearchRepository
) {

    suspend operator fun invoke(query: String) = repository.searchCity(query)
}