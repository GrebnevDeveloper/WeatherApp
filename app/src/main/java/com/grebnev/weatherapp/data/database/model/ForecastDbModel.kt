package com.grebnev.weatherapp.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "forecast")
data class ForecastDbModel(
    @PrimaryKey val cityId: Long,
    val currentWeather: WeatherDbModel,
    val upcoming: List<WeatherDbModel>,
)