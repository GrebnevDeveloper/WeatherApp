package com.grebnev.weatherapp.data.database.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.grebnev.weatherapp.data.database.model.CityDbModel

@Database(entities = [CityDbModel::class], version = 1, exportSchema = false)
abstract class FavouriteCitiesDatabase : RoomDatabase() {

    abstract fun favouriteCitiesDao(): FavouriteCitiesDao

    companion object {
        private const val DATABASE_NAME = "favouriteDatabase"
        private var INSTANCE: FavouriteCitiesDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): FavouriteCitiesDatabase {
            INSTANCE?.let { return it }

            synchronized(LOCK) {
                INSTANCE?.let { return it }

                val database = Room.databaseBuilder(
                    context = context,
                    klass = FavouriteCitiesDatabase::class.java,
                    name = DATABASE_NAME
                ).build()

                INSTANCE = database
                return database
            }
        }
    }
}