package com.grebnev.weatherapp.di.module

import com.grebnev.weatherapp.data.database.dao.WeatherFavouriteCitiesDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.ktor.client.HttpClient
import org.junit.Assert.assertNotNull
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
class DataModuleTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var httpClient: HttpClient

    @Inject
    lateinit var database: WeatherFavouriteCitiesDatabase

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `should provide HttpClient`() {
        assertNotNull(httpClient)
    }

    @Test
    fun `should provide database`() {
        assertNotNull(database)
    }

    @Test
    fun `should provide DAOs`() {
        assertNotNull(database.favouriteCitiesDao())
        assertNotNull(database.forecastCitiesDao())
        assertNotNull(database.metadataDao())
    }
}