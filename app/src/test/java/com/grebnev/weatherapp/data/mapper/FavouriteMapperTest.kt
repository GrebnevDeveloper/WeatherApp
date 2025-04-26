package com.grebnev.weatherapp.data.mapper

import com.grebnev.weatherapp.data.database.model.CityDbModel
import com.grebnev.weatherapp.domain.entity.City
import org.junit.Assert.assertEquals
import org.junit.Test

class FavouriteMapperTest {
    @Test
    fun `City to CityDbModel conversion`() {
        val city = City(1L, "Moscow", "Russia")
        val dbModel = city.toCityDbModel()

        assertEquals(city.id, dbModel.id)
        assertEquals(city.name, dbModel.name)
        assertEquals(city.country, dbModel.country)
    }

    @Test
    fun `CityDbModel to City conversion`() {
        val dbModel = CityDbModel(1L, "Moscow", "Russia")
        val city = dbModel.toCity()

        assertEquals(dbModel.id, city.id)
        assertEquals(dbModel.name, city.name)
        assertEquals(dbModel.country, city.country)
    }

    @Test
    fun `List of CityDbModel to Cities conversion`() {
        val dbModels =
            listOf(
                CityDbModel(1L, "Moscow", "Russia"),
                CityDbModel(2L, "London", "UK"),
            )
        val cities = dbModels.toCities()

        assertEquals(2, cities.size)
        assertEquals(dbModels[0].id, cities[0].id)
        assertEquals(dbModels[1].name, cities[1].name)
    }
}