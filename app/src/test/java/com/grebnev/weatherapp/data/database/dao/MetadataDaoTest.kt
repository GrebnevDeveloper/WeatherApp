package com.grebnev.weatherapp.data.database.dao

import androidx.room.Room
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class MetadataDaoTest {
    private lateinit var database: WeatherFavouriteCitiesDatabase
    private lateinit var dao: MetadataDao

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    RuntimeEnvironment.getApplication(),
                    WeatherFavouriteCitiesDatabase::class.java,
                ).allowMainThreadQueries()
                .build()
        dao = database.metadataDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `getTimeLastUpdateForecast should return null when no value exists`() {
        val result = dao.getTimeLastUpdateForecast()
        assertNull(result)
    }

    @Test
    fun `getTimeLastUpdateForecast should return valid timestamp when exists`() =
        runTest {
            val testTime = System.currentTimeMillis()
            dao.updateTimeLastUpdateForecast(time = testTime)

            val result = dao.getTimeLastUpdateForecast()

            assertEquals(testTime, result)
        }

    @Test
    fun `getTimeLastUpdateForecast should return null when invalid format`() =
        runTest {
            database.query(
                "INSERT INTO metadata VALUES(?, ?)",
                arrayOf(
                    "last_update_forecast",
                    "not_a_number",
                ),
            )

            val result = dao.getTimeLastUpdateForecast()

            assertNull(result)
        }

    @Test
    fun `updateTimeLastUpdateForecast should store timestamp correctly`() =
        runTest {
            val testTime = System.currentTimeMillis()

            dao.updateTimeLastUpdateForecast(time = testTime)

            val storedValue =
                database
                    .query(
                        "SELECT value FROM metadata WHERE keyMetadata = ?",
                        arrayOf("last_update_forecast"),
                    ).use { cursor ->
                        if (cursor.moveToFirst()) cursor.getString(0) else null
                    }

            assertEquals(testTime.toString(), storedValue)
        }

    @Test
    fun `updateTimeLastUpdateForecast with custom key should store separately`() =
        runTest {
            val customKey = "custom_key"
            val testTime = System.currentTimeMillis()

            dao.updateTimeLastUpdateForecast(timeLatUpdateKey = customKey, time = testTime)

            val defaultKeyValue = dao.getTimeLastUpdateForecast()
            assertNull(defaultKeyValue)

            val customValue =
                database
                    .query(
                        "SELECT value FROM metadata WHERE keyMetadata = ?",
                        arrayOf(customKey),
                    ).use { cursor ->
                        if (cursor.moveToFirst()) cursor.getString(0)?.toLong() else null
                    }

            assertEquals(testTime, customValue)
        }

    @Test
    fun `updateTimeLastUpdateForecast should overwrite existing value`() =
        runTest {
            val initialTime = System.currentTimeMillis() - 1000
            dao.updateTimeLastUpdateForecast(time = initialTime)

            val newTime = System.currentTimeMillis()
            dao.updateTimeLastUpdateForecast(time = newTime)

            val result = dao.getTimeLastUpdateForecast()
            assertEquals(newTime, result)
            assertNotEquals(initialTime, result)
        }
}