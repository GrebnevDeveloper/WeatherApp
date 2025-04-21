package com.grebnev.weatherapp.presentation.favourite

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.grebnev.weatherapp.domain.entity.City
import com.grebnev.weatherapp.presentation.extensions.componentScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultFavouriteComponent
    @AssistedInject
    constructor(
        private val favouriteStoreFactory: FavouriteStoreFactory,
        @Assisted("onCityItemClicked") private val onCityItemClicked: (City) -> Unit,
        @Assisted("onSearchClicked") private val onSearchClicked: () -> Unit,
        @Assisted("onAddToFavouriteClicked") private val onAddToFavouriteClicked: () -> Unit,
        @Assisted("component") component: ComponentContext,
    ) : FavouriteComponent,
        ComponentContext by component {
        private val store = instanceKeeper.getStore { favouriteStoreFactory.create() }
        private val scope = componentScope()

        init {
            scope.launch {
                store.labels.collect {
                    when (it) {
                        FavouriteStore.Label.AddFavouriteClicked -> {
                            onAddToFavouriteClicked()
                        }

                        is FavouriteStore.Label.CityItemClicked -> {
                            onCityItemClicked(it.city)
                        }

                        FavouriteStore.Label.SearchClicked -> {
                            onSearchClicked()
                        }
                    }
                }
            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        override val model: StateFlow<FavouriteStore.State> = store.stateFlow

        override fun onSearchClick() {
            store.accept(FavouriteStore.Intent.SearchClicked)
        }

        override fun onAddToFavouriteClick() {
            store.accept(FavouriteStore.Intent.AddFavouriteClicked)
        }

        override fun onCityItemClick(city: City) {
            store.accept(FavouriteStore.Intent.CityItemClicked(city))
        }

        override fun onRetryLoadWeatherClick() {
            store.accept(FavouriteStore.Intent.RetryLoadWeatherClicked)
        }

        @AssistedFactory
        interface Factory {
            fun create(
                @Assisted("onCityItemClicked") onCityItemClicked: (City) -> Unit,
                @Assisted("onSearchClicked") onSearchClicked: () -> Unit,
                @Assisted("onAddToFavouriteClicked") onAddToFavouriteClicked: () -> Unit,
                @Assisted("component") component: ComponentContext,
            ): DefaultFavouriteComponent
        }
    }