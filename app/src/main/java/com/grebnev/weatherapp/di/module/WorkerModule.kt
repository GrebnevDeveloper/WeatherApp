package com.grebnev.weatherapp.di.module

import com.grebnev.weatherapp.data.workers.ChildWeatherWorkerFactory
import com.grebnev.weatherapp.data.workers.RefreshWeatherWorker
import com.grebnev.weatherapp.di.key.WorkerKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
interface WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(RefreshWeatherWorker::class)
    fun bindRefreshWeatherWorkerFactory(factory: RefreshWeatherWorker.Factory): ChildWeatherWorkerFactory
}