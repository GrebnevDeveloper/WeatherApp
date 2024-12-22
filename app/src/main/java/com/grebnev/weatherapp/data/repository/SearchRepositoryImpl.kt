package com.grebnev.weatherapp.data.repository

import com.grebnev.weatherapp.data.mapper.toCities
import com.grebnev.weatherapp.data.network.api.ApiService
import com.grebnev.weatherapp.domain.entity.City
import com.grebnev.weatherapp.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SearchRepository {
    override suspend fun searchCity(query: String): List<City> =
        apiService.searchCity(query).toCities()
}