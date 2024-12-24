package com.grebnev.weatherapp.presentation.details

import kotlinx.coroutines.flow.StateFlow

interface DetailsComponent {

    val model: StateFlow<DetailsStore.State>

    fun onBackClick()

    fun onFavouriteStatusClick()
}