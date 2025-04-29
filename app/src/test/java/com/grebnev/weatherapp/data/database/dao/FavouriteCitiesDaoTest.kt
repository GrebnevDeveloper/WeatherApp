package com.grebnev.weatherapp.data.database.dao

import androidx.room.Room
import app.cash.turbine.test
import com.grebnev.weatherapp.data.database.model.CityDbModel
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class FavouriteCitiesDaoTest {
    private lateinit var database: WeatherFavouriteCitiesDatabase
    private lateinit var dao: FavouriteCitiesDao

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    RuntimeEnvironment.getApplication(),
                    WeatherFavouriteCitiesDatabase::class.java,
                ).allowMainThreadQueries()
                .build()
        dao = database.favouriteCitiesDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `getFavouriteCities should emit empty list initially`() =
        runTest {
            dao.getFavouriteCities().test {
                assertEquals(emptyList<CityDbModel>(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `addToFavouriteCities should add city to favourites`() =
        runTest {
            val city = CityDbModel(1, "Paris", "France")
            dao.addToFavouriteCities(city)

            dao.getFavouriteCities().test {
                assertEquals(listOf(city), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `removeFromFavouriteCities should remove city from favourites`() =
        runTest {
            val city = CityDbModel(1, "Paris", "France")
            dao.addToFavouriteCities(city)
            dao.removeFromFavouriteCities(city.id)

            dao.getFavouriteCities().test {
                assertEquals(emptyList<CityDbModel>(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `observeIsFavourite should emit false for non-existent city`() =
        runTest {
            dao.observeIsFavourite(999).test {
                assertEquals(false, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `observeIsFavourite should emit true when city is added`() =
        runTest {
            val city = CityDbModel(1, "Paris", "France")
            val flow = dao.observeIsFavourite(city.id)

            flow.test {
                assertEquals(false, awaitItem())

                dao.addToFavouriteCities(city)
                assertEquals(true, awaitItem())

                dao.removeFromFavouriteCities(city.id)
                assertEquals(false, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `adding duplicate city should replace existing one`() =
        runTest {
            val city1 = CityDbModel(1, "Paris", "France")
            val city2 = CityDbModel(1, "Paris", "FR")

            dao.addToFavouriteCities(city1)
            dao.addToFavouriteCities(city2)

            dao.getFavouriteCities().test {
                val cities = awaitItem()
                assertEquals(1, cities.size)
                assertEquals("FR", cities[0].country)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getFavouriteCities should emit updates when data changes`() =
        runTest {
            val city1 = CityDbModel(1, "Paris", "France")
            val city2 = CityDbModel(2, "Berlin", "Germany")

            dao.getFavouriteCities().test {
                assertEquals(emptyList<CityDbModel>(), awaitItem())

                dao.addToFavouriteCities(city1)
                assertEquals(listOf(city1), awaitItem())

                dao.addToFavouriteCities(city2)
                assertEquals(listOf(city1, city2), awaitItem())

                dao.removeFromFavouriteCities(city1.id)
                assertEquals(listOf(city2), awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }
}