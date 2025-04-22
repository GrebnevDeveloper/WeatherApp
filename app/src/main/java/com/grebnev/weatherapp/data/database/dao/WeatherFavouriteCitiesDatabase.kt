package com.grebnev.weatherapp.data.database.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grebnev.weatherapp.data.database.converter.WeatherConverter
import com.grebnev.weatherapp.data.database.model.CityDbModel
import com.grebnev.weatherapp.data.database.model.ForecastDbModel
import com.grebnev.weatherapp.data.database.model.MetadataDbModel
import com.grebnev.weatherapp.data.database.model.WeatherDbModel

@Database(
    entities = [
        CityDbModel::class,
        WeatherDbModel::class,
        ForecastDbModel::class,
        MetadataDbModel::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(WeatherConverter::class)
abstract class WeatherFavouriteCitiesDatabase : RoomDatabase() {
    abstract fun favouriteCitiesDao(): FavouriteCitiesDao

    abstract fun forecastCitiesDao(): ForecastDao

    abstract fun metadataDao(): MetadataDao

    companion object {
        private const val DATABASE_NAME = "weatherFavouriteCitiesDatabase"
        private var instance: WeatherFavouriteCitiesDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): WeatherFavouriteCitiesDatabase {
            instance?.let { return it }

            synchronized(LOCK) {
                instance?.let { return it }

                val database =
                    Room
                        .databaseBuilder(
                            context = context,
                            klass = WeatherFavouriteCitiesDatabase::class.java,
                            name = DATABASE_NAME,
                        ).build()

                instance = database
                return database
            }
        }
    }
}