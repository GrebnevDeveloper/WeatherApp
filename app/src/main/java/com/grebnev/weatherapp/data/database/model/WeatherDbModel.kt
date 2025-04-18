package com.grebnev.weatherapp.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "weathers")
data class WeatherDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tempC: Float,
    val conditionText: String,
    val conditionUrl: String,
    val date: Long,
)