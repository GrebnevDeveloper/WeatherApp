package com.grebnev.weatherapp.data.mapper

import com.grebnev.weatherapp.data.network.dto.CityDto
import com.grebnev.weatherapp.domain.entity.City

fun CityDto.toCity(): City = City(id, name, country)

fun List<CityDto>.toCities(): List<City> = map { it.toCity() }