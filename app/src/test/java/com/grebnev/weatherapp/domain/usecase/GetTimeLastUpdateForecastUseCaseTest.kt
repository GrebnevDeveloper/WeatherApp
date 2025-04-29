package com.grebnev.weatherapp.domain.usecase

import com.grebnev.weatherapp.core.wrappers.OutdatedDataException
import com.grebnev.weatherapp.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GetTimeLastUpdateForecastUseCaseTest {
    private lateinit var useCase: GetTimeLastUpdateForecastUseCase
    private lateinit var repository: WeatherRepository

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetTimeLastUpdateForecastUseCase(repository)
    }

    @Test
    fun `invoke should return formatted time when repository returns timestamp`() =
        runTest {
            val testTime = System.currentTimeMillis()
            coEvery { repository.getTimeLastUpdateForecast() } returns testTime

            val expectedFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(testTime))

            val result = useCase()

            assertEquals(expectedFormat, result)
        }

    @Test(expected = OutdatedDataException::class)
    fun `invoke should throw when repository throws OutdatedDataException`() =
        runTest {
            coEvery { repository.getTimeLastUpdateForecast() } throws OutdatedDataException("No data")

            useCase()
        }

    @Test
    fun `invoke should use IO dispatcher for repository call`() =
        runTest {
            val testTime = System.currentTimeMillis()
            coEvery { repository.getTimeLastUpdateForecast() } returns testTime

            useCase()

            coVerify { repository.getTimeLastUpdateForecast() }
        }

    @Test
    fun `invoke should format time according to specified pattern`() =
        runTest {
            val testTime = 1672531200000L
            coEvery { repository.getTimeLastUpdateForecast() } returns testTime

            val expectedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(testTime))

            val result = useCase()

            assertEquals(expectedTime, result)
        }
}