package com.grebnev.weatherapp.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.grebnev.weatherapp.domain.entity.City
import com.grebnev.weatherapp.presentation.details.DefaultDetailsComponent
import com.grebnev.weatherapp.presentation.favourite.DefaultFavouriteComponent
import com.grebnev.weatherapp.presentation.search.DefaultSearchComponent
import com.grebnev.weatherapp.presentation.search.OpenReason
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.serialization.Serializable

class DefaultRootComponent @AssistedInject constructor(
    private val detailsComponentFactory: DefaultDetailsComponent.Factory,
    private val searchComponentFactory: DefaultSearchComponent.Factory,
    private val favouriteComponentFactory: DefaultFavouriteComponent.Factory,
    @Assisted("component") component: ComponentContext
) : RootComponent, ComponentContext by component {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Favourite,
        handleBackButton = true,
        childFactory = ::child
    )

    @OptIn(DelicateDecomposeApi::class)
    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child {
        return when (config) {
            is Config.Details -> {
                val component = detailsComponentFactory.create(
                    city = config.city,
                    onBackClicked = {
                        navigation.pop()
                    },
                    component = componentContext
                )
                RootComponent.Child.Details(component)
            }

            Config.Favourite -> {
                val component = favouriteComponentFactory.create(
                    onCityItemClicked = {
                        navigation.push(Config.Details(it))
                    },
                    onSearchClicked = {
                        navigation.push(Config.Search(OpenReason.SEARCH_QUERY))
                    },
                    onAddToFavouriteClicked = {
                        navigation.push(Config.Search(OpenReason.ADD_TO_FAVORITE))
                    },
                    component = componentContext
                )
                RootComponent.Child.Favourite(component)
            }

            is Config.Search -> {
                val component = searchComponentFactory.create(
                    openReason = config.openReason,
                    onBackClicked = {
                        navigation.pop()
                    },
                    onForecastOpened = {
                        navigation.push(Config.Details(it))
                    },
                    onFavouriteSaved = {
                        navigation.pop()
                    },
                    component = componentContext
                )
                RootComponent.Child.Search(component)
            }
        }
    }

    @Serializable
    sealed interface Config {

        @Serializable
        data object Favourite : Config

        @Serializable
        data class Search(
            val openReason: OpenReason
        ) : Config

        @Serializable
        data class Details(
            val city: City
        ) : Config
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted("component") component: ComponentContext
        ): DefaultRootComponent
    }
}