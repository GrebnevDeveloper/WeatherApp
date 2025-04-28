package com.grebnev.weatherapp.di.module

import com.grebnev.weatherapp.data.repository.WeatherRepositoryImpl
import com.grebnev.weatherapp.domain.repository.WeatherRepository
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
class RepositoryModuleTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var weatherRepository: WeatherRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `should provide WeatherRepository`() {
        assertNotNull(weatherRepository)
        assertTrue(weatherRepository is WeatherRepositoryImpl)
    }
}