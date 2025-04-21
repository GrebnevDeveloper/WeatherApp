package com.grebnev.weatherapp.presentation.details

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

class DefaultDetailsComponent
    @AssistedInject
    constructor(
        private val detailsStoreFactory: DetailsStoreFactory,
        @Assisted("city") private val city: City,
        @Assisted("onBackClicked") private val onBackClicked: () -> Unit,
        @Assisted("component") component: ComponentContext,
    ) : DetailsComponent,
        ComponentContext by component {
        private val store = instanceKeeper.getStore { detailsStoreFactory.create(city) }

        private val scope = componentScope()

        init {
            scope.launch {
                store.labels.collect {
                    when (it) {
                        DetailsStore.Label.BackClicked -> {
                            onBackClicked()
                        }
                    }
                }
            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        override val model: StateFlow<DetailsStore.State> = store.stateFlow

        override fun onBackClick() {
            store.accept(DetailsStore.Intent.BackClicked)
        }

        override fun onFavouriteStatusClick() {
            store.accept(DetailsStore.Intent.FavouriteStatusClicked)
        }

        override fun onRetryLoadForecastClick() {
            store.accept(DetailsStore.Intent.RetryLoadForecastClicked)
        }

        @AssistedFactory
        interface Factory {
            fun create(
                @Assisted("city") city: City,
                @Assisted("onBackClicked") onBackClicked: () -> Unit,
                @Assisted("component") component: ComponentContext,
            ): DefaultDetailsComponent
        }
    }