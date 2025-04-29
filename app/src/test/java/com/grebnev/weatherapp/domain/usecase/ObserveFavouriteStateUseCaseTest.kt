package com.grebnev.weatherapp.domain.usecase

import app.cash.turbine.test
import com.grebnev.weatherapp.domain.repository.FavouriteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObserveFavouriteStateUseCaseTest {
    private lateinit var useCase: ObserveFavouriteStateUseCase
    private lateinit var repository: FavouriteRepository

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ObserveFavouriteStateUseCase(repository)
    }

    @Test
    fun `invoke should return true when city is favourite`() =
        runTest {
            val cityId = 1L
            coEvery { repository.observeIsFavouriteCity(cityId) } returns flowOf(true)

            useCase(cityId).test {
                assertEquals(true, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `invoke should return false when city is not favourite`() =
        runTest {
            val cityId = 2L
            coEvery { repository.observeIsFavouriteCity(cityId) } returns flowOf(false)

            useCase(cityId).test {
                assertEquals(false, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `invoke should propagate flow errors from repository`() =
        runTest {
            val cityId = 3L
            val exception = RuntimeException("Test error")
            coEvery { repository.observeIsFavouriteCity(cityId) } returns flow { throw exception }

            useCase(cityId).test {
                assertEquals(exception, awaitError())
            }
        }

    @Test
    fun `invoke should delegate to repository with correct cityId`() =
        runTest {
            val cityId = 4L
            coEvery { repository.observeIsFavouriteCity(cityId) } returns flowOf(true)

            useCase(cityId).test {
                awaitItem()
                awaitComplete()
            }

            coVerify { repository.observeIsFavouriteCity(cityId) }
        }

    @Test
    fun `invoke should emit multiple values when repository updates`() =
        runTest {
            val cityId = 5L
            coEvery { repository.observeIsFavouriteCity(cityId) } returns flowOf(false, true, false)

            useCase(cityId).test {
                assertEquals(false, awaitItem())
                assertEquals(true, awaitItem())
                assertEquals(false, awaitItem())
                awaitComplete()
            }
        }
}