package com.grebnev.weatherapp.data.network.api

import com.grebnev.weatherapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.Locale

object ApiFactory {
    private const val KEY_PARAM = "key"
    private const val LANG_PARAM = "lang"
    private const val BASE_URL = "https://api.weatherapi.com/v1/"

    fun createHttpClient(): HttpClient =
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }

            defaultRequest {
                url {
                    takeFrom(BASE_URL)
                    parameters.append(KEY_PARAM, BuildConfig.WEATHER_API_KEY)
                    parameters.append(LANG_PARAM, Locale.getDefault().language)
                }
            }

            install(Logging) {
                level = LogLevel.HEADERS
            }
        }
}