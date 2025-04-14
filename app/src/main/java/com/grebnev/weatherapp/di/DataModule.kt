package com.grebnev.weatherapp.di

import android.content.Context
import com.grebnev.weatherapp.data.database.dao.FavouriteCitiesDao
import com.grebnev.weatherapp.data.database.dao.FavouriteCitiesDatabase
import com.grebnev.weatherapp.data.network.api.ApiFactory
import com.grebnev.weatherapp.data.repository.FavouriteRepositoryImpl
import com.grebnev.weatherapp.data.repository.SearchRepositoryImpl
import com.grebnev.weatherapp.data.repository.WeatherRepositoryImpl
import com.grebnev.weatherapp.domain.repository.FavouriteRepository
import com.grebnev.weatherapp.domain.repository.SearchRepository
import com.grebnev.weatherapp.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    @Singleton
    fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository

    @Binds
    @Singleton
    fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    companion object {
        @Provides
        @Singleton
        fun provideApiService() = ApiFactory.apiService

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context,
        ): FavouriteCitiesDatabase = FavouriteCitiesDatabase.getInstance(context)

        @Provides
        @Singleton
        fun provideFavouriteCitiesDao(database: FavouriteCitiesDatabase): FavouriteCitiesDao =
            database.favouriteCitiesDao()
    }
}