package com.grebnev.weatherapp.data.repository

import app.cash.turbine.test
import com.grebnev.weatherapp.data.database.dao.FavouriteCitiesDao
import com.grebnev.weatherapp.data.database.model.CityDbModel
import com.grebnev.weatherapp.domain.entity.City
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteRepositoryImplTest {
    private lateinit var favouriteCitiesDao: FavouriteCitiesDao
    private lateinit var favouriteRepository: FavouriteRepositoryImpl

    @Before
    fun setUp() {
        favouriteCitiesDao = mockk()
    }

    @Test
    fun `favouriteCities should emit mapped cities from DAO`() =
        runTest {
            val dbModels =
                listOf(
                    CityDbModel(1L, "Moscow", "Russia"),
                    CityDbModel(2L, "London", "UK"),
                )
            coEvery { favouriteCitiesDao.getFavouriteCities() } returns flowOf(dbModels)
            favouriteRepository = FavouriteRepositoryImpl(favouriteCitiesDao)

            favouriteRepository.favouriteCities.test {
                val cities = awaitItem()
                assertEquals(2, cities.size)
                assertEquals("Moscow", cities[0].name)
                assertEquals("UK", cities[1].country)
                awaitComplete()
            }

            coVerify { favouriteCitiesDao.getFavouriteCities() }
        }

    @Test
    fun `observeIsFavouriteCity should delegate to DAO`() =
        runTest {
            val cityId = 1L
            coEvery { favouriteCitiesDao.getFavouriteCities() } returns flowOf(emptyList())
            favouriteRepository = FavouriteRepositoryImpl(favouriteCitiesDao)
            coEvery { favouriteCitiesDao.observeIsFavourite(cityId) } returns flowOf(true)

            favouriteRepository.observeIsFavouriteCity(cityId).test {
                assertEquals(true, awaitItem())
                awaitComplete()
            }

            coVerify { favouriteCitiesDao.observeIsFavourite(cityId) }
        }

    @Test
    fun `addToFavouriteCity should convert City to CityDbModel and call DAO`() =
        runTest {
            val city = City(1L, "Paris", "France")
            val expectedDbModel = CityDbModel(1L, "Paris", "France")
            coEvery { favouriteCitiesDao.getFavouriteCities() } returns flowOf(emptyList())
            favouriteRepository = FavouriteRepositoryImpl(favouriteCitiesDao)
            coEvery { favouriteRepository.addToFavouriteCity(city) } just Runs

            favouriteRepository.addToFavouriteCity(city)

            coVerify { favouriteCitiesDao.addToFavouriteCities(expectedDbModel) }
        }

    @Test
    fun `removeFromFavouriteCity should call DAO with correct ID`() =
        runTest {
            val cityId = 1L
            coEvery { favouriteCitiesDao.getFavouriteCities() } returns flowOf(emptyList())
            favouriteRepository = FavouriteRepositoryImpl(favouriteCitiesDao)
            coEvery { favouriteRepository.removeFromFavouriteCity(cityId) } just Runs

            favouriteRepository.removeFromFavouriteCity(cityId)

            coVerify { favouriteCitiesDao.removeFromFavouriteCities(cityId) }
        }

    @Test
    fun `empty favouriteCities should emit empty list`() =
        runTest {
            coEvery { favouriteCitiesDao.getFavouriteCities() } returns flowOf(emptyList())
            favouriteRepository = FavouriteRepositoryImpl(favouriteCitiesDao)
            coEvery { favouriteCitiesDao.getFavouriteCities() } returns flowOf(emptyList())

            favouriteRepository.favouriteCities.test {
                assertTrue(awaitItem().isEmpty())
                awaitComplete()
            }

            coVerify { favouriteCitiesDao.getFavouriteCities() }
        }
}