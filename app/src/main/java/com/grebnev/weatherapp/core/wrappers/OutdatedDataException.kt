package com.grebnev.weatherapp.core.wrappers

class OutdatedDataException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)