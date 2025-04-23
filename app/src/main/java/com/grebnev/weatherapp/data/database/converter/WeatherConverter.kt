package com.grebnev.weatherapp.data.database.converter

import androidx.room.TypeConverter
import com.grebnev.weatherapp.data.database.model.WeatherDbModel
import kotlinx.serialization.json.Json

class WeatherConverter {
    private val json =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    @TypeConverter
    fun fromWeatherDbModel(weather: WeatherDbModel?): String? = weather?.let { json.encodeToString(it) }

    @TypeConverter
    fun toWeatherDbModel(jsonString: String?): WeatherDbModel? = jsonString?.let { json.decodeFromString(it) }

    @TypeConverter
    fun fromWeatherList(list: List<WeatherDbModel>?): String? = list?.let { json.encodeToString(it) }

    @TypeConverter
    fun toWeatherList(jsonString: String?): List<WeatherDbModel>? =
        jsonString?.let { json.decodeFromString(it) }
}