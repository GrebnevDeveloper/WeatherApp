package com.grebnev.weatherapp.data.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.grebnev.weatherapp.data.database.dao.FavouriteCitiesDao
import com.grebnev.weatherapp.data.database.dao.ForecastDao
import com.grebnev.weatherapp.data.database.dao.MetadataDao
import com.grebnev.weatherapp.data.database.model.ForecastDbModel
import com.grebnev.weatherapp.data.mapper.toForecastDbModel
import com.grebnev.weatherapp.data.network.api.ApiService
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class RefreshWeatherWorkerTest {
    @MockK
    private lateinit var workerParameters: WorkerParameters

    @MockK
    private lateinit var mockFavouriteCitiesDao: FavouriteCitiesDao

    @MockK
    private lateinit var mockForecastDao: ForecastDao

    @MockK
    private lateinit var mockMetadataDao: MetadataDao

    @MockK
    private lateinit var mockApiService: ApiService

    private lateinit var worker: RefreshWeatherWorker

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Unconfined)

        worker =
            TestListenableWorkerBuilder<RefreshWeatherWorker>(
                RuntimeEnvironment.getApplication(),
            ).setWorkerFactory(createTestWorkerFactory()).build()
    }

    private fun createTestWorkerFactory(): WorkerFactory =
        object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters,
            ): ListenableWorker? =
                when (workerClassName) {
                    RefreshWeatherWorker::class.qualifiedName ->
                        RefreshWeatherWorker(
                            appContext,
                            workerParameters,
                            mockFavouriteCitiesDao,
                            mockForecastDao,
                            mockMetadataDao,
                            mockApiService,
                        )
                    else -> null
                }
        }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `doWork should return success when forecast refresh completes successfully`() =
        runTest {
            val cityId = 1L
            val forecast = mockk<ForecastDbModel>()

            every { mockFavouriteCitiesDao.getFavouriteCities() } returns
                flowOf(listOf(mockk { every { id } returns cityId }))
            coEvery { mockApiService.loadWeatherForecast("id:$cityId") } returns
                mockk {
                    every { toForecastDbModel(cityId) } returns forecast
                }
            coEvery { mockForecastDao.updateForecastForCity(forecast) } just Runs
            coEvery { mockMetadataDao.updateTimeLastUpdateForecast(time = any()) } just Runs

            worker.doWork()

            coVerify { mockFavouriteCitiesDao.getFavouriteCities() }
            coVerify { mockApiService.loadWeatherForecast("id:$cityId") }
        }

    @Test
    fun `doWork should return retry when api call fails`() =
        runTest {
            val cityId = 1L

            coEvery { mockFavouriteCitiesDao.getFavouriteCities() } returns
                flowOf(listOf(mockk { every { id } returns cityId }))
            coEvery { mockApiService.loadWeatherForecast("id:$cityId") } throws
                RuntimeException("API failure")

            val result = worker.doWork()

            assertEquals(ListenableWorker.Result.retry(), result)
            coVerify(exactly = 0) { mockMetadataDao.updateTimeLastUpdateForecast(time = any()) }
            coVerify(exactly = 0) { mockForecastDao.updateForecastForCity(any()) }
        }

    @Test
    fun `doWork should handle empty favourite cities list`() =
        runTest {
            coEvery { mockFavouriteCitiesDao.getFavouriteCities() } returns flowOf(emptyList())
            coEvery { mockMetadataDao.updateTimeLastUpdateForecast(time = any()) } just Runs

            val result = worker.doWork()

            assertEquals(ListenableWorker.Result.success(), result)
            coVerify { mockMetadataDao.updateTimeLastUpdateForecast(time = any()) }
            coVerify(exactly = 0) { mockApiService.loadWeatherForecast(any()) }
            coVerify(exactly = 0) { mockForecastDao.updateForecastForCity(any()) }
        }

    @Test
    fun `factory should create RefreshWeatherWorker instance`() {
        val factory =
            RefreshWeatherWorker.Factory(
                mockFavouriteCitiesDao,
                mockForecastDao,
                mockMetadataDao,
                mockApiService,
            )

        val createdWorker = factory.create(RuntimeEnvironment.getApplication(), workerParameters)

        assert(createdWorker is RefreshWeatherWorker)
    }
}