package com.grebnev.weatherapp.data.repository

import com.grebnev.weatherapp.data.network.api.ApiService
import com.grebnev.weatherapp.data.network.dto.CityDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class SearchRepositoryImplTest {
    private lateinit var apiService: ApiService
    private lateinit var searchRepository: SearchRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        searchRepository = SearchRepositoryImpl(apiService)
    }

    @Test
    fun `searchCity should return mapped cities from API`() =
        runTest {
            val query = "Moscow"
            val cityDtos =
                listOf(
                    CityDto(1L, "Moscow", "Russia"),
                    CityDto(2L, "Moskva", "Belarus"),
                )
            coEvery { apiService.searchCity(query) } returns cityDtos

            val result = searchRepository.searchCity(query)

            assertEquals(2, result.size)
            assertEquals("Moscow", result[0].name)
            assertEquals("Belarus", result[1].country)

            coVerify { apiService.searchCity(query) }
        }

    @Test
    fun `searchCity should return empty list when API returns empty`() =
        runTest {
            val query = "Unknown"
            coEvery { apiService.searchCity(query) } returns emptyList()

            val result = searchRepository.searchCity(query)

            assertTrue(result.isEmpty())
            coVerify { apiService.searchCity(query) }
        }

    @Test
    fun `searchCity should propagate API exceptions`() =
        runTest {
            val query = "Error"
            val expectedException = RuntimeException("API error")
            coEvery { apiService.searchCity(query) } throws expectedException

            try {
                searchRepository.searchCity(query)
                fail("Expected exception to be thrown")
            } catch (e: RuntimeException) {
                assertEquals("API error", e.message)
            }

            coVerify { apiService.searchCity(query) }
        }
}