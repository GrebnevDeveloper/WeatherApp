package com.grebnev.weatherapp.domain.repository

import com.grebnev.weatherapp.domain.entity.City
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {

    val favouriteCities: Flow<List<City>>

    fun observeIsFavouriteCity(cityId: Long): Flow<Boolean>

    suspend fun addToFavouriteCity(city: City)

    suspend fun removeFromFavouriteCity(cityId: Long)
}
