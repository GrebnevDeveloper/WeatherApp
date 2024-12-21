package com.grebnev.weatherapp.presentation.root

import com.arkivanov.decompose.ComponentContext

class DefaultRootComponent(
    component: ComponentContext
) : RootComponent, ComponentContext by component