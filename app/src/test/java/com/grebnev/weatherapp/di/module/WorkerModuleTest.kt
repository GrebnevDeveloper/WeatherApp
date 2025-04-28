package com.grebnev.weatherapp.di.module

import androidx.work.ListenableWorker
import com.grebnev.weatherapp.data.workers.ChildWeatherWorkerFactory
import com.grebnev.weatherapp.data.workers.RefreshWeatherWorker
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class WorkerModuleTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var workerFactories:
        Map<Class<out ListenableWorker>, @JvmSuppressWildcards ChildWeatherWorkerFactory>

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `should provide RefreshWeatherWorker factory`() {
        val factory = workerFactories[RefreshWeatherWorker::class.java]
        assertNotNull(factory)
        assertTrue(factory is RefreshWeatherWorker.Factory)
    }
}