package com.grebnev.weatherapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.grebnev.weatherapp.data.network.api.ApiFactory
import com.grebnev.weatherapp.presentation.ui.theme.WeatherAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val apiService = ApiFactory.apiService
        CoroutineScope(Dispatchers.Main).launch {
            Log.d(
                "MainActivity", """
            WeatherCurrent: ${apiService.loadWeatherCurrent("London")}
            WeatherForecast: ${apiService.loadWeatherForecast("London")}
            SearchCity: ${apiService.loadWeatherCurrent("London")}
        """.trimIndent()
            )
        }
        setContent {
            WeatherAppTheme {
            }
        }
    }
}