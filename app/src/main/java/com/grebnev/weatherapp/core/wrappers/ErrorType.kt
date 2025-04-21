package com.grebnev.weatherapp.core.wrappers

enum class ErrorType(
    val type: String,
) {
    NETWORK_ERROR("network_error"),
    DATABASE_ERROR("database_error"),
    UNKNOWN_ERROR("unknown_error"),
}