package com.grebnev.weatherapp.di.module

import android.content.Context
import com.grebnev.weatherapp.data.database.dao.FavouriteCitiesDao
import com.grebnev.weatherapp.data.database.dao.ForecastDao
import com.grebnev.weatherapp.data.database.dao.WeatherFavouriteCitiesDatabase
import com.grebnev.weatherapp.data.network.api.ApiFactory
import com.grebnev.weatherapp.data.network.api.ApiService
import com.grebnev.weatherapp.data.network.api.ApiServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = ApiFactory.createHttpClient()

    @Provides
    @Singleton
    fun provideApiService(client: HttpClient): ApiService = ApiServiceImpl(client)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): WeatherFavouriteCitiesDatabase = WeatherFavouriteCitiesDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideFavouriteCitiesDao(database: WeatherFavouriteCitiesDatabase): FavouriteCitiesDao =
        database.favouriteCitiesDao()

    @Provides
    @Singleton
    fun provideForecastDao(database: WeatherFavouriteCitiesDatabase): ForecastDao =
        database.forecastCitiesDao()
}