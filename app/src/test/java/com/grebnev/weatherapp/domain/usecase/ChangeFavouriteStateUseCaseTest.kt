package com.grebnev.weatherapp.domain.usecase

import com.grebnev.weatherapp.domain.entity.City
import com.grebnev.weatherapp.domain.repository.FavouriteRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ChangeFavouriteStateUseCaseTest {
    private lateinit var useCase: ChangeFavouriteStateUseCase
    private lateinit var repository: FavouriteRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = ChangeFavouriteStateUseCase(repository)
    }

    @Test
    fun `addToFavourite should delegate to repository`() =
        runTest {
            val testCity = City(id = 1L, name = "Moscow", country = "Russia")

            useCase.addToFavourite(testCity)

            coVerify { repository.addToFavouriteCity(testCity) }
        }

    @Test
    fun `removeFromFavourite should delegate to repository`() =
        runTest {
            val testCityId = 1L

            useCase.removeFromFavourite(testCityId)

            coVerify { repository.removeFromFavouriteCity(testCityId) }
        }

    @Test
    fun `addToFavourite should pass correct city data`() =
        runTest {
            val testCity = City(id = 2L, name = "London", country = "UK")

            useCase.addToFavourite(testCity)

            coVerify {
                repository.addToFavouriteCity(
                    withArg { city ->
                        assertEquals(2L, city.id)
                        assertEquals("London", city.name)
                        assertEquals("UK", city.country)
                    },
                )
            }
        }

    @Test
    fun `removeFromFavourite should pass correct city ID`() =
        runTest {
            val testCityId = 3L

            useCase.removeFromFavourite(testCityId)

            coVerify {
                repository.removeFromFavouriteCity(
                    match { it == testCityId },
                )
            }
        }
}