package com.grebnev.weatherapp.data.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "weathers",
    foreignKeys = [
        ForeignKey(
            entity = ForecastDbModel::class,
            parentColumns = ["cityId"],
            childColumns = ["forecastCityId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class WeatherDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val forecastCityId: Long,
    val tempC: Float,
    val conditionText: String,
    val conditionUrl: String,
    val date: Long,
)