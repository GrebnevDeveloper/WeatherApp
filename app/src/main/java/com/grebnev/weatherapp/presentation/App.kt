package com.grebnev.weatherapp.presentation

import android.app.Application
import com.grebnev.weatherapp.data.workers.RefreshWeatherWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        RefreshWeatherWorker.runRefreshWeatherWorker(this)
    }
}