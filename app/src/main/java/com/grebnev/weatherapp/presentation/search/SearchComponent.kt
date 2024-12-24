package com.grebnev.weatherapp.presentation.search

import com.grebnev.weatherapp.domain.entity.City
import kotlinx.coroutines.flow.StateFlow

interface SearchComponent {

    val model: StateFlow<SearchStore.State>

    fun onBackClick()

    fun onCityClick(city: City)

    fun onSearchClick()

    fun changedSearchQuery(query: String)
}