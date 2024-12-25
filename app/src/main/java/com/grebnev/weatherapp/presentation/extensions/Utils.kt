package com.grebnev.weatherapp.presentation.extensions

import kotlin.math.roundToInt

fun Float.toTempCString(): String = "${roundToInt()}°C"