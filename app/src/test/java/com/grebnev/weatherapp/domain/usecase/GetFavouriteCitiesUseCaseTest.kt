package com.grebnev.weatherapp.domain.usecase

import app.cash.turbine.test
import com.grebnev.weatherapp.domain.entity.City
import com.grebnev.weatherapp.domain.repository.FavouriteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetFavouriteCitiesUseCaseTest {
    private lateinit var useCase: GetFavouriteCitiesUseCase
    private lateinit var repository: FavouriteRepository

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetFavouriteCitiesUseCase(repository)
    }

    @Test
    fun `invoke should return flow of favourite cities`() =
        runTest {
            val testCities =
                listOf(
                    City(id = 1L, name = "Moscow", country = "Russia"),
                    City(id = 2L, name = "London", country = "UK"),
                )
            coEvery { repository.favouriteCities } returns flowOf(testCities)

            useCase().test {
                val result = awaitItem()
                assertEquals(2, result.size)
                assertEquals("Moscow", result[0].name)
                assertEquals("UK", result[1].country)
                awaitComplete()
            }
        }

    @Test
    fun `invoke should return empty flow when no favourite cities`() =
        runTest {
            coEvery { repository.favouriteCities } returns flowOf(emptyList())

            useCase().test {
                val result = awaitItem()
                assertTrue(result.isEmpty())
                awaitComplete()
            }
        }

    @Test
    fun `invoke should propagate flow errors`() =
        runTest {
            val exception = RuntimeException("Test error")
            coEvery { repository.favouriteCities } returns flow { throw exception }

            useCase().test {
                assertEquals(exception, awaitError())
            }
        }

    @Test
    fun `invoke should delegate to repository favouriteCities`() =
        runTest {
            val testCities = listOf(City(id = 1L, name = "Paris", country = "France"))
            coEvery { repository.favouriteCities } returns flowOf(testCities)

            useCase().test {
                awaitItem()
                awaitComplete()
            }

            coVerify { repository.favouriteCities }
        }
}