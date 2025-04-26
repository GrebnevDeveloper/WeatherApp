package com.grebnev.weatherapp.data.database.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import com.grebnev.weatherapp.data.database.model.CityDbModel
import com.grebnev.weatherapp.data.database.model.ForecastDbModel
import com.grebnev.weatherapp.data.database.model.WeatherDbModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ForecastDaoTest {
    private lateinit var database: WeatherFavouriteCitiesDatabase
    private lateinit var forecastDao: ForecastDao
    private lateinit var citiesDao: FavouriteCitiesDao

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    RuntimeEnvironment.getApplication(),
                    WeatherFavouriteCitiesDatabase::class.java,
                ).allowMainThreadQueries()
                .build()
        forecastDao = database.forecastCitiesDao()
        citiesDao = database.favouriteCitiesDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `getForecastForCity should return null when no forecast exists`() {
        val result = forecastDao.getForecastForCity(1)
        assertNull(result)
    }

    @Test
    fun `getCurrentWeatherForCity should return null when no forecast exists`() {
        val result = forecastDao.getCurrentWeatherForCity(1)
        assertNull(result)
    }

    @Test
    fun `updateForecastForCity should insert new forecast`() =
        runTest {
            val cityId = 1L
            citiesDao.addToFavouriteCities(CityDbModel(cityId, "Moscow", "Russia"))
            val weather = createTestWeather(cityId)
            val forecast = ForecastDbModel(cityId, weather, listOf(weather))

            forecastDao.updateForecastForCity(forecast)

            val savedForecast = forecastDao.getForecastForCity(cityId)
            assertNotNull(savedForecast)
            assertEquals(cityId, savedForecast?.cityId)
        }

    @Test
    fun `updateForecastForCity should replace existing forecast`() =
        runTest {
            val cityId = 1L
            citiesDao.addToFavouriteCities(CityDbModel(cityId, "Moscow", "Russia"))
            val initialWeather = createTestWeather(cityId, tempC = 20f)
            val updatedWeather = createTestWeather(cityId, tempC = 25f)

            forecastDao.updateForecastForCity(ForecastDbModel(cityId, initialWeather, listOf(initialWeather)))

            forecastDao.updateForecastForCity(ForecastDbModel(cityId, updatedWeather, listOf(updatedWeather)))

            val savedForecast = forecastDao.getForecastForCity(cityId)
            assertEquals(25f, savedForecast?.currentWeather?.tempC)
        }

    @Test
    fun `getCurrentWeatherForCity should return correct weather`() =
        runTest {
            val cityId = 1L
            citiesDao.addToFavouriteCities(CityDbModel(cityId, "Moscow", "Russia"))
            val weather = createTestWeather(cityId, tempC = 22f)
            forecastDao.updateForecastForCity(ForecastDbModel(cityId, weather, listOf(weather)))

            val currentWeather = forecastDao.getCurrentWeatherForCity(cityId)

            assertNotNull(currentWeather)
            assertEquals(22f, currentWeather?.tempC)
        }

    @Test
    fun `updateForecastForCity should fail with foreign key constraint when city doesn't exist`() {
        val nonExistentCityId = 999L
        val weather = createTestWeather(nonExistentCityId)
        val forecast = ForecastDbModel(nonExistentCityId, weather, listOf(weather))

        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking { forecastDao.updateForecastForCity(forecast) }
        }
    }

    private fun createTestWeather(
        cityId: Long,
        tempC: Float = 20f,
        conditionText: String = "Sunny",
        date: Long = System.currentTimeMillis(),
    ): WeatherDbModel =
        WeatherDbModel(
            forecastCityId = cityId,
            tempC = tempC,
            conditionText = conditionText,
            conditionUrl = "http://example.com",
            date = date,
        )
}