package com.grebnev.weatherapp.data.mapper

import com.grebnev.weatherapp.data.network.dto.CityDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchMapperTest {
    @Test
    fun `CityDto to City conversion should map all fields`() {
        val dto =
            CityDto(
                id = 1L,
                name = "Moscow",
                country = "Russia",
            )

        val city = dto.toCity()

        assertEquals(dto.id, city.id)
        assertEquals(dto.name, city.name)
        assertEquals(dto.country, city.country)
    }

    @Test
    fun `List of CityDto to Cities conversion should map all items`() {
        val dtos =
            listOf(
                CityDto(id = 1L, name = "Moscow", country = "Russia"),
                CityDto(id = 2L, name = "London", country = "UK"),
                CityDto(id = 3L, name = "Paris", country = "France"),
            )

        val cities = dtos.toCities()

        assertEquals(dtos.size, cities.size)

        dtos.forEachIndexed { index, dto ->
            assertEquals(dto.id, cities[index].id)
            assertEquals(dto.name, cities[index].name)
            assertEquals(dto.country, cities[index].country)
        }
    }

    @Test
    fun `Empty List of CityDto to Cities conversion should return empty list`() {
        val emptyDtos = emptyList<CityDto>()

        val cities = emptyDtos.toCities()

        assertTrue(cities.isEmpty())
    }
}