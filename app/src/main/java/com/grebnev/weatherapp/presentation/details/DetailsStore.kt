package com.grebnev.weatherapp.presentation.details

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.grebnev.weatherapp.domain.entity.City
import com.grebnev.weatherapp.domain.entity.Forecast
import com.grebnev.weatherapp.domain.usecase.ChangeFavouriteStateUseCase
import com.grebnev.weatherapp.domain.usecase.GetForecastUseCase
import com.grebnev.weatherapp.domain.usecase.GetTimeLastUpdateForecastUseCase
import com.grebnev.weatherapp.domain.usecase.ObserveFavouriteStateUseCase
import com.grebnev.weatherapp.presentation.details.DetailsStore.Intent
import com.grebnev.weatherapp.presentation.details.DetailsStore.Label
import com.grebnev.weatherapp.presentation.details.DetailsStore.State
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

interface DetailsStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object BackClicked : Intent

        data object FavouriteStatusClicked : Intent

        data object RetryLoadForecastClicked : Intent
    }

    data class State(
        val city: City,
        val isFavourite: Boolean,
        val forecastState: ForecastState,
    ) {
        sealed interface ForecastState {
            data object Initial : ForecastState

            data object Loading : ForecastState

            data object Error : ForecastState

            data class Loaded(
                val forecast: Forecast,
            ) : ForecastState

            data class LoadedFromCache(
                val timeLastUpdate: String,
                val forecast: Forecast,
            ) : ForecastState
        }
    }

    sealed interface Label {
        data object BackClicked : Label
    }
}

class DetailsStoreFactory
    @Inject
    constructor(
        private val storeFactory: StoreFactory,
        private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase,
        private val getForecastUseCase: GetForecastUseCase,
        private val observeFavouriteStateUseCase: ObserveFavouriteStateUseCase,
        private val getTimeLastUpdateForecastUseCase: GetTimeLastUpdateForecastUseCase,
    ) {
        fun create(city: City): DetailsStore =
            object :
                DetailsStore,
                Store<Intent, State, Label> by storeFactory.create(
                    name = "DetailsStore",
                    initialState =
                        State(
                            city = city,
                            isFavourite = false,
                            forecastState = State.ForecastState.Initial,
                        ),
                    bootstrapper =
                        BootstrapperImpl(
                            city = city,
                        ),
                    executorFactory = ::ExecutorImpl,
                    reducer = ReducerImpl,
                ) {}

        private sealed interface Action {
            data class FavouriteStatusChanged(
                val isFavourite: Boolean,
            ) : Action

            data class ForecastLoaded(
                val forecast: Forecast,
            ) : Action

            data class ForecastLoadedFromCache(
                val timeLastUpdate: String,
                val forecast: Forecast,
            ) : Action

            data object ForecastLoading : Action

            data object ForecastLoadingError : Action
        }

        private sealed interface Msg {
            data class FavouriteStatusChanged(
                val isFavourite: Boolean,
            ) : Msg

            data class ForecastLoaded(
                val forecast: Forecast,
            ) : Msg

            data class ForecastLoadedFromCache(
                val timeLastUpdate: String,
                val forecast: Forecast,
            ) : Msg

            data object ForecastLoading : Msg

            data object ForecastLoadingError : Msg
        }

        private inner class BootstrapperImpl(
            private val city: City,
        ) : CoroutineBootstrapper<Action>() {
            override fun invoke() {
                scope.launch {
                    observeFavouriteStateUseCase(city.id).collect {
                        dispatch(Action.FavouriteStatusChanged(it))
                    }
                }
                scope.launch {
                    dispatch(Action.ForecastLoading)
                    try {
                        getForecastUseCase(city.id).collect { forecastCity ->
                            if (!forecastCity.isDataFromCache) {
                                dispatch(Action.ForecastLoaded(forecastCity))
                            } else {
                                dispatch(
                                    Action.ForecastLoadedFromCache(
                                        timeLastUpdate = getTimeLastUpdateForecastUseCase(),
                                        forecast = forecastCity,
                                    ),
                                )
                            }
                        }
                    } catch (exception: Exception) {
                        Timber.e(exception, "Exception occurred in details store")
                        dispatch(Action.ForecastLoadingError)
                    }
                }
            }
        }

        private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
            override fun executeIntent(intent: Intent) {
                when (intent) {
                    Intent.BackClicked -> {
                        publish(Label.BackClicked)
                    }

                    Intent.FavouriteStatusClicked -> {
                        scope.launch {
                            val state = state()
                            if (state.isFavourite) {
                                changeFavouriteStateUseCase.removeFromFavourite(state.city.id)
                            } else {
                                changeFavouriteStateUseCase.addToFavourite(state.city)
                            }
                        }
                    }

                    Intent.RetryLoadForecastClicked -> {
                        scope.launch {
                            try {
                                dispatch(Msg.ForecastLoading)
                                val state = state()
                                getForecastUseCase(state.city.id).collect { forecastCity ->
                                    if (!forecastCity.isDataFromCache) {
                                        dispatch(Msg.ForecastLoaded(forecastCity))
                                    } else {
                                        dispatch(
                                            Msg.ForecastLoadedFromCache(
                                                timeLastUpdate = getTimeLastUpdateForecastUseCase(),
                                                forecast = forecastCity,
                                            ),
                                        )
                                    }
                                }
                            } catch (exception: Exception) {
                                Timber.e(exception, "Exception occurred in retry load forecast")
                                dispatch(Msg.ForecastLoadingError)
                            }
                        }
                    }
                }
            }

            override fun executeAction(action: Action) {
                when (action) {
                    is Action.FavouriteStatusChanged -> {
                        dispatch(Msg.FavouriteStatusChanged(action.isFavourite))
                    }

                    is Action.ForecastLoaded -> {
                        dispatch(Msg.ForecastLoaded(action.forecast))
                    }

                    is Action.ForecastLoadedFromCache -> {
                        dispatch(
                            Msg.ForecastLoadedFromCache(
                                timeLastUpdate = action.timeLastUpdate,
                                forecast = action.forecast,
                            ),
                        )
                    }

                    Action.ForecastLoading -> {
                        dispatch(Msg.ForecastLoading)
                    }

                    Action.ForecastLoadingError -> {
                        dispatch(Msg.ForecastLoadingError)
                    }
                }
            }
        }

        private object ReducerImpl : Reducer<State, Msg> {
            override fun State.reduce(msg: Msg): State =
                when (msg) {
                    is Msg.FavouriteStatusChanged -> {
                        copy(isFavourite = msg.isFavourite)
                    }

                    is Msg.ForecastLoaded -> {
                        copy(forecastState = State.ForecastState.Loaded(msg.forecast))
                    }

                    is Msg.ForecastLoadedFromCache -> {
                        copy(
                            forecastState =
                                State.ForecastState.LoadedFromCache(
                                    timeLastUpdate = msg.timeLastUpdate,
                                    forecast = msg.forecast,
                                ),
                        )
                    }

                    Msg.ForecastLoading -> {
                        copy(forecastState = State.ForecastState.Loading)
                    }

                    Msg.ForecastLoadingError -> {
                        copy(forecastState = State.ForecastState.Error)
                    }
                }
        }
    }