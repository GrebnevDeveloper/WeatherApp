package com.grebnev.weatherapp.domain.repository

import com.grebnev.weatherapp.domain.entity.City

interface SearchRepository {

    suspend fun searchCity(query: String): List<City>
}