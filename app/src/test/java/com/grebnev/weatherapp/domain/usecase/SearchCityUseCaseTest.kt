package com.grebnev.weatherapp.domain.usecase

import com.grebnev.weatherapp.domain.entity.City
import com.grebnev.weatherapp.domain.repository.SearchRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SearchCityUseCaseTest {
    private lateinit var useCase: SearchCityUseCase
    private lateinit var repository: SearchRepository

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SearchCityUseCase(repository)
    }

    @Test
    fun `invoke should return cities from repository`() =
        runTest {
            val query = "Moscow"
            val expectedCities =
                listOf(
                    City(id = 1, name = "Moscow", country = "Russia"),
                    City(id = 2, name = "Moskva", country = "Serbia"),
                )
            coEvery { repository.searchCity(query) } returns expectedCities

            val result = useCase(query)

            assertEquals(expectedCities, result)
            coVerify { repository.searchCity(query) }
        }

    @Test
    fun `invoke should return empty list when no results`() =
        runTest {
            val query = "Unknown"
            coEvery { repository.searchCity(query) } returns emptyList()

            val result = useCase(query)

            assertTrue(result.isEmpty())
        }

    @Test(expected = IOException::class)
    fun `invoke should propagate repository IOExceptions`() =
        runTest {
            val query = "Error"
            coEvery { repository.searchCity(query) } throws IOException("Network error")

            useCase(query)
        }

    @Test
    fun `invoke should pass exact query to repository`() =
        runTest {
            val query = "New York"
            coEvery { repository.searchCity(query) } returns emptyList()

            useCase(query)

            coVerify { repository.searchCity(query) }
        }

    @Test
    fun `invoke should return correct city data`() =
        runTest {
            val query = "London"
            val expectedCity = City(id = 3, name = "London", country = "UK")
            coEvery { repository.searchCity(query) } returns listOf(expectedCity)

            val result = useCase(query)

            assertEquals(1, result.size)
            assertEquals("London", result[0].name)
            assertEquals("UK", result[0].country)
        }
}