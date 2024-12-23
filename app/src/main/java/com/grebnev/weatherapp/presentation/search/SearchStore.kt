package com.grebnev.weatherapp.presentation.search

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.grebnev.weatherapp.domain.entity.City
import com.grebnev.weatherapp.domain.usecase.ChangeFavouriteStateUseCase
import com.grebnev.weatherapp.domain.usecase.SearchCityUseCase
import com.grebnev.weatherapp.presentation.search.SearchStore.Intent
import com.grebnev.weatherapp.presentation.search.SearchStore.Label
import com.grebnev.weatherapp.presentation.search.SearchStore.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SearchStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data object BackClicked : Intent

        data object SearchClicked : Intent

        data class CityClicked(
            val city: City
        ) : Intent

        data class ChangedSearchQuery(
            val searchQuery: String
        ) : Intent
    }

    data class State(
        val searchQuery: String,
        val searchState: SearchState
    ) {

        sealed interface SearchState {

            data object Initial : SearchState

            data object Loading : SearchState

            data object Error : SearchState

            data object EmptyResult : SearchState

            data class SuccessLoaded(
                val cities: List<City>
            ) : SearchState

        }
    }

    sealed interface Label {

        data object BackClicked : Label

        data object SavedFavouriteCity : Label

        data class OpenForecast(
            val city: City
        ) : Label
    }
}

class SearchStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val searchCityUseCase: SearchCityUseCase,
    private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase
) {

    fun create(openReason: OpenReason): SearchStore =
        object : SearchStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SearchStore",
            initialState = State(
                searchQuery = "",
                searchState = State.SearchState.Initial
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { ExecutorImpl(openReason) },
            reducer = ReducerImpl
        ) {}

    private sealed interface Action

    private sealed interface Msg {

        data object SearchResultLoading : Msg

        data object SearchResultError : Msg

        data class ChangedSearchQuery(
            val searchQuery: String
        ) : Msg

        data class SearchResultLoaded(
            val cities: List<City>
        ) : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
        }
    }

    private inner class ExecutorImpl(
        private val openReason: OpenReason
    ) : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        private var searchJob: Job? = null

        override fun executeIntent(intent: Intent) {
            when (intent) {
                Intent.BackClicked -> {
                    publish(Label.BackClicked)
                }

                is Intent.ChangedSearchQuery -> {
                    dispatch(Msg.ChangedSearchQuery(intent.searchQuery))
                }

                is Intent.CityClicked -> {
                    when (openReason) {
                        OpenReason.ADD_TO_FAVORITE -> {
                            scope.launch(Dispatchers.Main.immediate) {
                                changeFavouriteStateUseCase.addToFavourite(intent.city)
                            }
                            publish(Label.SavedFavouriteCity)
                        }

                        OpenReason.SEARCH_QUERY -> {
                            publish(Label.OpenForecast(intent.city))
                        }
                    }
                }

                Intent.SearchClicked -> {
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        dispatch(Msg.SearchResultLoading)
                        try {
                            val cities = searchCityUseCase(state().searchQuery)
                            dispatch(Msg.SearchResultLoaded(cities))
                        } catch (e: Exception) {
                            dispatch(Msg.SearchResultError)
                        }
                    }
                }
            }
        }

        override fun executeAction(action: Action) {
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.ChangedSearchQuery -> {
                copy(searchQuery = msg.searchQuery)
            }

            Msg.SearchResultError -> {
                copy(searchState = State.SearchState.Error)
            }

            is Msg.SearchResultLoaded -> {
                val searchState = if (msg.cities.isEmpty()) {
                    State.SearchState.EmptyResult
                } else {
                    State.SearchState.SuccessLoaded(msg.cities)
                }
                copy(searchState = searchState)
            }

            Msg.SearchResultLoading -> {
                copy(searchState = State.SearchState.Loading)
            }
        }
    }
}
