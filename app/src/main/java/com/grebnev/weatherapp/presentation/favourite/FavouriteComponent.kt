package com.grebnev.weatherapp.presentation.favourite

import com.grebnev.weatherapp.domain.entity.City
import kotlinx.coroutines.flow.StateFlow

interface FavouriteComponent {

    val model: StateFlow<FavouriteStore.State>

    fun onSearchClick()

    fun onAddToFavouriteClick()

    fun onCityItemClick(city: City)
}