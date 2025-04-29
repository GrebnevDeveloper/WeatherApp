package com.grebnev.weatherapp.data.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class WeatherWorkerFactory
    @Inject
    constructor(
        private val weatherWorkerProvides:
            @JvmSuppressWildcards Map<Class<out ListenableWorker>, Provider<ChildWeatherWorkerFactory>>,
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters,
        ): ListenableWorker? {
            return when (workerClassName) {
                RefreshWeatherWorker::class.qualifiedName -> {
                    val childWorkerFactory = weatherWorkerProvides[RefreshWeatherWorker::class.java]?.get()
                    return childWorkerFactory?.create(appContext, workerParameters)
                }

                else -> null
            }
        }
    }