package com.grebnev.weatherapp.presentation.favourite

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.grebnev.weatherapp.domain.entity.City
import com.grebnev.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.grebnev.weatherapp.domain.usecase.GetFavouriteCitiesUseCase
import com.grebnev.weatherapp.domain.usecase.GetTimeLastUpdateForecastUseCase
import com.grebnev.weatherapp.presentation.favourite.FavouriteStore.Intent
import com.grebnev.weatherapp.presentation.favourite.FavouriteStore.Label
import com.grebnev.weatherapp.presentation.favourite.FavouriteStore.State
import com.grebnev.weatherapp.presentation.favourite.FavouriteStore.State.CityItem
import com.grebnev.weatherapp.presentation.favourite.FavouriteStore.State.WeatherState.Loaded
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

interface FavouriteStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object SearchClicked : Intent

        data object AddFavouriteClicked : Intent

        data object RetryLoadWeatherClicked : Intent

        data class CityItemClicked(
            val city: City,
        ) : Intent
    }

    data class State(
        val cityItems: List<CityItem>,
        val timeLastUpdate: String? = null,
    ) {
        data class CityItem(
            val city: City,
            val weatherState: WeatherState,
        )

        sealed interface WeatherState {
            data object Initial : WeatherState

            data object Loading : WeatherState

            data object Error : WeatherState

            data class Loaded(
                val tempC: Float,
                val conditionIconUrl: String,
            ) : WeatherState
        }
    }

    sealed interface Label {
        data object SearchClicked : Label

        data object AddFavouriteClicked : Label

        data class CityItemClicked(
            val city: City,
        ) : Label
    }
}

class FavouriteStoreFactory
    @Inject
    constructor(
        private val storeFactory: StoreFactory,
        private val getFavouriteCitiesUseCase: GetFavouriteCitiesUseCase,
        private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
        private val getTimeLastUpdateForecastUseCase: GetTimeLastUpdateForecastUseCase,
    ) {
        fun create(): FavouriteStore =
            object :
                FavouriteStore,
                Store<Intent, State, Label> by storeFactory.create(
                    name = "FavouriteStore",
                    initialState = State(listOf()),
                    bootstrapper = BootstrapperImpl(),
                    executorFactory = ::ExecutorImpl,
                    reducer = ReducerImpl,
                ) {}

        private sealed interface Action {
            data class FavouriteCitiesLoaded(
                val cities: List<City>,
            ) : Action
        }

        private sealed interface Msg {
            data class FavouriteCitiesLoaded(
                val cities: List<City>,
            ) : Msg

            data class WeatherLoaded(
                val cityId: Long,
                val tempC: Float,
                val conditionIconUrl: String,
            ) : Msg

            data class WeatherLoadedFromCache(
                val timeLastUpdate: String,
                val cityId: Long,
                val tempC: Float,
                val conditionIconUrl: String,
            ) : Msg

            data class WeatherLoadingError(
                val cityId: Long,
            ) : Msg

            data class WeatherIsLoading(
                val cityId: Long,
            ) : Msg
        }

        private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
            override fun invoke() {
                scope.launch {
                    getFavouriteCitiesUseCase().collect {
                        dispatch(Action.FavouriteCitiesLoaded(it))
                    }
                }
            }
        }

        private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
            override fun executeIntent(intent: Intent) {
                when (intent) {
                    Intent.AddFavouriteClicked -> {
                        publish(Label.AddFavouriteClicked)
                    }

                    is Intent.CityItemClicked -> {
                        publish(Label.CityItemClicked(intent.city))
                    }

                    Intent.SearchClicked -> {
                        publish(Label.SearchClicked)
                    }

                    is Intent.RetryLoadWeatherClicked -> {
                        val state = state()
                        state.cityItems.forEach { cityItem ->
                            scope.launch {
                                loadWeatherForCity(cityItem.city)
                            }
                        }
                    }
                }
            }

            override fun executeAction(action: Action) {
                when (action) {
                    is Action.FavouriteCitiesLoaded -> {
                        val cities = action.cities
                        dispatch(Msg.FavouriteCitiesLoaded(cities))
                        cities.forEach {
                            scope.launch {
                                loadWeatherForCity(it)
                            }
                        }
                    }
                }
            }

            private suspend fun loadWeatherForCity(city: City) {
                dispatch(Msg.WeatherIsLoading(city.id))
                try {
                    getCurrentWeatherUseCase(city.id).collect { weatherCity ->
                        if (!weatherCity.isDataFromCache) {
                            dispatch(
                                Msg.WeatherLoaded(
                                    cityId = city.id,
                                    tempC = weatherCity.tempC,
                                    conditionIconUrl = weatherCity.conditionUrl,
                                ),
                            )
                        } else {
                            dispatch(
                                Msg.WeatherLoadedFromCache(
                                    timeLastUpdate = getTimeLastUpdateForecastUseCase(),
                                    cityId = city.id,
                                    tempC = weatherCity.tempC,
                                    conditionIconUrl = weatherCity.conditionUrl,
                                ),
                            )
                        }
                    }
                } catch (exception: Exception) {
                    Timber.e(exception, "Exception occurred in favourite store")
                    dispatch(Msg.WeatherLoadingError(city.id))
                }
            }
        }

        private object ReducerImpl : Reducer<State, Msg> {
            override fun State.reduce(msg: Msg): State =
                when (msg) {
                    is Msg.FavouriteCitiesLoaded -> {
                        copy(
                            cityItems =
                                msg.cities.map {
                                    CityItem(
                                        city = it,
                                        weatherState = State.WeatherState.Initial,
                                    )
                                },
                        )
                    }

                    is Msg.WeatherIsLoading -> {
                        copy(
                            timeLastUpdate = null,
                            cityItems =
                                cityItems.map {
                                    if (it.city.id == msg.cityId) {
                                        it.copy(weatherState = State.WeatherState.Loading)
                                    } else {
                                        it
                                    }
                                },
                        )
                    }

                    is Msg.WeatherLoaded -> {
                        copy(
                            cityItems =
                                cityItems.map {
                                    if (it.city.id == msg.cityId) {
                                        it.copy(
                                            weatherState =
                                                Loaded(
                                                    tempC = msg.tempC,
                                                    conditionIconUrl = msg.conditionIconUrl,
                                                ),
                                        )
                                    } else {
                                        it
                                    }
                                },
                        )
                    }

                    is Msg.WeatherLoadedFromCache -> {
                        copy(
                            timeLastUpdate = msg.timeLastUpdate,
                            cityItems =
                                cityItems.map {
                                    if (it.city.id == msg.cityId) {
                                        it.copy(
                                            weatherState =
                                                Loaded(
                                                    tempC = msg.tempC,
                                                    conditionIconUrl = msg.conditionIconUrl,
                                                ),
                                        )
                                    } else {
                                        it
                                    }
                                },
                        )
                    }

                    is Msg.WeatherLoadingError -> {
                        copy(
                            cityItems =
                                cityItems.map {
                                    if (it.city.id == msg.cityId) {
                                        it.copy(weatherState = State.WeatherState.Error)
                                    } else {
                                        it
                                    }
                                },
                        )
                    }
                }
        }
    }