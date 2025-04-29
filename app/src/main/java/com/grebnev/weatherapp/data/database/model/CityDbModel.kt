package com.grebnev.weatherapp.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_cities")
data class CityDbModel(
    @PrimaryKey val id: Long,
    val name: String,
    val country: String
)
