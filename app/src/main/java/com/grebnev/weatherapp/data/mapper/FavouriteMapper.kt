package com.grebnev.weatherapp.data.mapper

import com.grebnev.weatherapp.data.database.model.CityDbModel
import com.grebnev.weatherapp.domain.entity.City

fun City.toCityDbModel(): CityDbModel = CityDbModel(id, name, country)

fun CityDbModel.toCity(): City = City(id, name, country)

fun List<CityDbModel>.toCities(): List<City> = map { it.toCity() }