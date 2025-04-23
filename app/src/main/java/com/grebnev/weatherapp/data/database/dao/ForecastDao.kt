package com.grebnev.weatherapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grebnev.weatherapp.data.database.model.ForecastDbModel
import com.grebnev.weatherapp.data.database.model.WeatherDbModel

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecast WHERE cityId = :cityId LIMIT 1")
    fun getForecastForCity(cityId: Long): ForecastDbModel

    @Query("SELECT currentWeather FROM forecast WHERE cityId = :cityId LIMIT 1")
    fun getCurrentWeatherForCity(cityId: Long): WeatherDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateForecastForCity(forecast: ForecastDbModel)
}