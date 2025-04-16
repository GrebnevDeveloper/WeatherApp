package com.grebnev.weatherapp.data.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.grebnev.weatherapp.data.database.dao.FavouriteCitiesDao
import com.grebnev.weatherapp.domain.repository.WeatherRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RefreshWeatherWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
    private val favouriteCitiesDao: FavouriteCitiesDao,
    private val weatherRepository: WeatherRepository,
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }

    companion object {
        private const val REFRESH_WEATHER_WORKER_NAME = "refresh_weather_worker"
        private const val REFRESH_TIMEOUT_MINUTES = 60L

        private val constraints =
            Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build()

        private fun makeRequest(): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<RefreshWeatherWorker>(
                REFRESH_TIMEOUT_MINUTES,
                TimeUnit.MINUTES,
            ).setConstraints(constraints)
                .build()

        fun runRefreshWeatherWorker(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                    REFRESH_WEATHER_WORKER_NAME,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    makeRequest(),
                )
        }
    }

    class Factory
        @Inject
        constructor(
            private val favouriteCitiesDao: FavouriteCitiesDao,
            private val weatherRepository: WeatherRepository,
        ) : ChildWeatherWorkerFactory {
            override fun create(
                context: Context,
                workerParameters: WorkerParameters,
            ): ListenableWorker =
                RefreshWeatherWorker(context, workerParameters, favouriteCitiesDao, weatherRepository)
        }
}