package com.grebnev.weatherapp.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metadata")
data class MetadataDbModel(
    @PrimaryKey val keyMetadata: String,
    val value: String,
)