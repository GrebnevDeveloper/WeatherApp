package com.grebnev.weatherapp.core.handlers

import com.grebnev.weatherapp.core.wrappers.ErrorType
import java.io.IOException
import java.sql.SQLException

object ErrorHandler {
    const val RETRY_TIMEOUT = 3000L
    const val MAX_COUNT_RETRY = 3L

    fun getErrorTypeByError(throwable: Throwable): ErrorType =
        when (throwable) {
            is IOException -> {
                ErrorType.NETWORK_ERROR
            }
            is SQLException -> {
                ErrorType.DATABASE_ERROR
            }

            else -> {
                ErrorType.UNKNOWN_ERROR
            }
        }
}