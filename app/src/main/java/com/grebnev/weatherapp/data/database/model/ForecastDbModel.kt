package com.grebnev.weatherapp.data.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "forecast",
    foreignKeys = [
        ForeignKey(
            entity = CityDbModel::class,
            parentColumns = ["id"],
            childColumns = ["cityId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ForecastDbModel(
    @PrimaryKey val cityId: Long,
    val currentWeather: WeatherDbModel,
    val upcoming: List<WeatherDbModel>,
)