package com.grebnev.weatherapp.data.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface ChildWeatherWorkerFactory {
    fun create(
        context: Context,
        workerParameters: WorkerParameters,
    ): ListenableWorker
}