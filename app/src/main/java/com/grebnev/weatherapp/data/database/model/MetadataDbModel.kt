package com.grebnev.weatherapp.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metadata")
data class MetadataDbModel(
    @PrimaryKey val keyMetadata: String,
    val value: String,
) {
    companion object {
        const val TIME_LAST_UPDATE_FORECAST_KEY = "last_update_forecast"
    }
}