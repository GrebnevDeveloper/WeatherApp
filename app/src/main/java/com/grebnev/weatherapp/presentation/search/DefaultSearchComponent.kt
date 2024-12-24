package com.grebnev.weatherapp.presentation.search

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

class DefaultSearchComponent @AssistedInject constructor(
    private val searchStoreFactory: SearchStoreFactory,
    @Assisted("openReason") private val openReason: OpenReason,
    @Assisted("onBackClicked") private val onBackClicked: () -> Unit,
    @Assisted("onForecastOpened") private val onForecastOpened: (City) -> Unit,
    @Assisted("onFavouriteSaved") private val onFavouriteSaved: () -> Unit,
    @Assisted("component") component: ComponentContext
) : SearchComponent, ComponentContext by component {

    private val store = instanceKeeper.getStore { searchStoreFactory.create(openReason) }

    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    SearchStore.Label.BackClicked -> {
                        onBackClicked()
                    }

                    is SearchStore.Label.OpenForecast -> {
                        onForecastOpened(it.city)
                    }

                    SearchStore.Label.SavedFavouriteCity -> {
                        onFavouriteSaved()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<SearchStore.State> = store.stateFlow

    override fun onBackClick() {
        store.accept(SearchStore.Intent.BackClicked)
    }

    override fun onCityClick(city: City) {
        store.accept(SearchStore.Intent.CityClicked(city))
    }

    override fun onSearchClick() {
        store.accept(SearchStore.Intent.SearchClicked)
    }

    override fun changedSearchQuery(query: String) {
        store.accept(SearchStore.Intent.ChangedSearchQuery(query))
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted("openReason") openReason: OpenReason,
            @Assisted("onBackClicked") onBackClicked: () -> Unit,
            @Assisted("onForecastOpened") onForecastOpened: (City) -> Unit,
            @Assisted("onFavouriteSaved") onFavouriteSaved: () -> Unit,
            @Assisted("component") component: ComponentContext
        ): DefaultSearchComponent
    }
}