package com.grebnev.weatherapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grebnev.weatherapp.data.database.model.MetadataDbModel
import timber.log.Timber

@Dao
abstract class MetadataDao {
    @Suppress("ktlint:standard:function-naming")
    @Query("SELECT value FROM metadata WHERE keyMetadata = :timeLastUpdateKey")
    protected abstract suspend fun _getTimeLastUpdateForecast(timeLastUpdateKey: String): String?

    @Suppress("ktlint:standard:function-naming")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun _updateTimeLastUpdateForecast(timeLastUpdate: MetadataDbModel)

    suspend fun getTimeLastUpdateForecast(): Long? =
        try {
            _getTimeLastUpdateForecast(TIME_LAST_UPDATE_FORECAST_KEY)?.toLong()
        } catch (exception: NumberFormatException) {
            Timber.e(exception, "Time last update conversion error")
            null
        }

    suspend fun updateTimeLastUpdateForecast(
        timeLatUpdateKey: String = TIME_LAST_UPDATE_FORECAST_KEY,
        time: Long,
    ) {
        _updateTimeLastUpdateForecast(
            MetadataDbModel(
                keyMetadata = timeLatUpdateKey,
                value = time.toString(),
            ),
        )
    }

    companion object {
        private const val TIME_LAST_UPDATE_FORECAST_KEY = "last_update_forecast"
    }
}