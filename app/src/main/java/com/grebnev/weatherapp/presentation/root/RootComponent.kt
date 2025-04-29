package com.grebnev.weatherapp.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.grebnev.weatherapp.presentation.details.DetailsComponent
import com.grebnev.weatherapp.presentation.favourite.FavouriteComponent
import com.grebnev.weatherapp.presentation.search.SearchComponent

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {

        data class Favourite(
            val favouriteComponent: FavouriteComponent
        ) : Child

        data class Search(
            val searchComponent: SearchComponent
        ) : Child

        data class Details(
            val detailsComponent: DetailsComponent
        ) : Child
    }
}