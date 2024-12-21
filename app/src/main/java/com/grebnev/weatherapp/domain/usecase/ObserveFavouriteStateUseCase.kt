package com.grebnev.weatherapp.domain.usecase

import com.grebnev.weatherapp.domain.repository.FavouriteRepository
import javax.inject.Inject

class ObserveFavouriteStateUseCase @Inject constructor(
    private val repository: FavouriteRepository
) {

    operator fun invoke(cityId: Long) = repository.observeIsFavouriteCity(cityId)
}