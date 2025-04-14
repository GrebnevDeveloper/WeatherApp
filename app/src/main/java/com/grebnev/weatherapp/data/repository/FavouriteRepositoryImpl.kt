package com.grebnev.weatherapp.data.repository

import com.grebnev.weatherapp.data.database.dao.FavouriteCitiesDao
import com.grebnev.weatherapp.data.mapper.toCities
import com.grebnev.weatherapp.data.mapper.toCityDbModel
import com.grebnev.weatherapp.domain.entity.City
import com.grebnev.weatherapp.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavouriteRepositoryImpl
    @Inject
    constructor(
        private val favouriteCitiesDao: FavouriteCitiesDao,
    ) : FavouriteRepository {
        override val favouriteCities: Flow<List<City>> =
            favouriteCitiesDao
                .getFavouriteCities()
                .map {
                    it.toCities()
                }

        override fun observeIsFavouriteCity(cityId: Long): Flow<Boolean> =
            favouriteCitiesDao.observeIsFavourite(cityId)

        override suspend fun addToFavouriteCity(city: City) =
            favouriteCitiesDao.addToFavouriteCities(city.toCityDbModel())

        override suspend fun removeFromFavouriteCity(cityId: Long) =
            favouriteCitiesDao.removeFromFavouriteCities(cityId)
    }