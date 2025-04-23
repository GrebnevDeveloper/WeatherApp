package com.grebnev.weatherapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grebnev.weatherapp.data.database.model.MetadataDbModel

@Dao
abstract class MetadataDao {
    @Suppress("ktlint:standard:function-naming")
    @Query("SELECT value FROM metadata WHERE keyMetadata = :timeLatUpdateKey")
    protected abstract fun _getTimeLastUpdateForecast(timeLatUpdateKey: String): String

    @Suppress("ktlint:standard:function-naming")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun _updateTimeLastUpdateForecast(timeLastUpdate: MetadataDbModel)

    fun getTimeLastUpdateForecast(): Long =
        _getTimeLastUpdateForecast(MetadataDbModel.TIME_LAST_UPDATE_FORECAST_KEY).toLong()

    fun updateTimeLastUpdateForecast(
        timeLatUpdateKey: String = MetadataDbModel.TIME_LAST_UPDATE_FORECAST_KEY,
        time: Long,
    ) {
        _updateTimeLastUpdateForecast(
            MetadataDbModel(
                keyMetadata = timeLatUpdateKey,
                value = time.toString(),
            ),
        )
    }
}