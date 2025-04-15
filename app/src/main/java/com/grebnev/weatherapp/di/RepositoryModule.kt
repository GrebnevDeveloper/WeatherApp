package com.grebnev.weatherapp.di

import com.grebnev.weatherapp.data.repository.FavouriteRepositoryImpl
import com.grebnev.weatherapp.data.repository.SearchRepositoryImpl
import com.grebnev.weatherapp.data.repository.WeatherRepositoryImpl
import com.grebnev.weatherapp.domain.repository.FavouriteRepository
import com.grebnev.weatherapp.domain.repository.SearchRepository
import com.grebnev.weatherapp.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository

    @Binds
    @Singleton
    fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository
}