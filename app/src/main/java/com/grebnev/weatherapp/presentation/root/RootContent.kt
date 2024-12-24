package com.grebnev.weatherapp.presentation.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.grebnev.weatherapp.presentation.details.DetailsContent
import com.grebnev.weatherapp.presentation.favourite.FavouriteContent
import com.grebnev.weatherapp.presentation.search.SearchContent
import com.grebnev.weatherapp.presentation.ui.theme.WeatherAppTheme

@Composable
fun RootContent(component: RootComponent) {
    WeatherAppTheme {
        Children(stack = component.stack) {
            when (val instance = it.instance) {
                is RootComponent.Child.Details -> {
                    DetailsContent(component = instance.detailsComponent)
                }

                is RootComponent.Child.Favourite -> {
                    FavouriteContent(component = instance.favouriteComponent)
                }

                is RootComponent.Child.Search -> {
                    SearchContent(component = instance.searchComponent)
                }
            }
        }
    }
}