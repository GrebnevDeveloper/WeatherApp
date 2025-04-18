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
import com.grebnev.weatherapp.data.database.dao.ForecastDao
import com.grebnev.weatherapp.data.mapper.toForecastDbModel
import com.grebnev.weatherapp.data.network.api.ApiService
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RefreshWeatherWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
    private val favouriteCitiesDao: FavouriteCitiesDao,
    private val forecastDao: ForecastDao,
    private val apiService: ApiService,
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        try {
            Timber.d("RefreshWeatherWorker run")
            refreshForecast()
            Timber.d("RefreshWeatherWorker success")
            return Result.success()
        } catch (exception: Exception) {
            Timber.e(exception, "RefreshWeatherWorker failed")
            return Result.retry()
        }
    }

    private suspend fun refreshForecast() {
        favouriteCitiesDao
            .getFavouriteCities()
            .first()
            .forEach { city ->
                Timber.d("RefreshWeatherWorker load ${city.name}")
                loadForecast(city.id)
            }
    }

    private suspend fun loadForecast(cityId: Long) {
        val forecast = apiService.loadWeatherForecast("$PREFIX_CITY_ID$cityId").toForecastDbModel(cityId)
        forecastDao.updateForecastForCity(forecast)
    }

    companion object {
        private const val REFRESH_WEATHER_WORKER_NAME = "refresh_weather_worker"
        private const val REFRESH_TIMEOUT_MINUTES = 15L
        private const val PREFIX_CITY_ID = "id:"

        private val constraints =
            Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build()

        private fun makePeriodicRequest(): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<RefreshWeatherWorker>(
                REFRESH_TIMEOUT_MINUTES,
                TimeUnit.MINUTES,
            ).setConstraints(constraints)
                .build()

        fun runRefreshWeatherWorker(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniquePeriodicWork(
                REFRESH_WEATHER_WORKER_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                makePeriodicRequest(),
            )
        }
    }

    class Factory
        @Inject
        constructor(
            private val favouriteCitiesDao: FavouriteCitiesDao,
            private val forecastDao: ForecastDao,
            private val apiService: ApiService,
        ) : ChildWeatherWorkerFactory {
            override fun create(
                context: Context,
                workerParameters: WorkerParameters,
            ): ListenableWorker =
                RefreshWeatherWorker(
                    context = context,
                    workerParameters = workerParameters,
                    favouriteCitiesDao = favouriteCitiesDao,
                    forecastDao = forecastDao,
                    apiService = apiService,
                )
        }
}