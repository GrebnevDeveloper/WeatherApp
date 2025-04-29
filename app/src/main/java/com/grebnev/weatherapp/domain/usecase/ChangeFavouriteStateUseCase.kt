package com.grebnev.weatherapp.domain.usecase

import com.grebnev.weatherapp.domain.entity.City
import com.grebnev.weatherapp.domain.repository.FavouriteRepository
import javax.inject.Inject

class ChangeFavouriteStateUseCase @Inject constructor(
    private val repository: FavouriteRepository
) {

    suspend fun addToFavourite(city: City) = repository.addToFavouriteCity(city)

    suspend fun removeFromFavourite(cityId: Long) = repository.removeFromFavouriteCity(cityId)
}