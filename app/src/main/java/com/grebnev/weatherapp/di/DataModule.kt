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

@Module
interface DataModule {

    @[Binds ApplicationScope]
    fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository

    @[Binds ApplicationScope]
    fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @[Binds ApplicationScope]
    fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    companion object {

        @[Provides ApplicationScope]
        fun provideApiService() = ApiFactory.apiService

        @[Provides ApplicationScope]
        fun provideDatabase(context: Context): FavouriteCitiesDatabase {
            return FavouriteCitiesDatabase.getInstance(context)
        }

        @[Provides ApplicationScope]
        fun provideFavouriteCitiesDao(database: FavouriteCitiesDatabase): FavouriteCitiesDao {
            return database.favouriteCitiesDao()
        }
    }
}