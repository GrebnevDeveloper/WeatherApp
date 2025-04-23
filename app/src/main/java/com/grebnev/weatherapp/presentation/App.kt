package com.grebnev.weatherapp.presentation

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.grebnev.weatherapp.BuildConfig
import com.grebnev.weatherapp.data.workers.RefreshWeatherWorker
import com.grebnev.weatherapp.data.workers.WeatherWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App :
    Application(),
    Configuration.Provider {
    @Inject
    lateinit var weatherWorkerFactory: WeatherWorkerFactory
    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(weatherWorkerFactory)
                .build()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        WorkManager.initialize(this, workManagerConfiguration)
        RefreshWeatherWorker.runRefreshWeatherWorker(this)
    }
}