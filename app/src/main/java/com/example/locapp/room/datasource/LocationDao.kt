package com.example.locapp.room.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.locapp.room.entity.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location)

    @Query("SELECT * FROM locations ORDER BY id DESC LIMIT 1")
    fun getLastLocation(): List<Location>

    @Query("SELECT * FROM locations WHERE NOT used_for_training ORDER BY id")
    fun getLocationsForTraining(): List<Location>

    @Query("SELECT DISTINCT place_id FROM locations ORDER BY id DESC LIMIT 3")
    fun getLastThreeEntries(): Flow<List<Int>>

    @Query("SELECT * FROM locations WHERE has_notification ORDER BY id DESC")
    fun getLocationsWithInProgressReview(): Flow<List<Location>>

    @Query("SELECT COUNT(1) FROM locations WHERE has_notification")
    fun getNumberOfNotifications(): Flow<Int>

    @Query("UPDATE locations SET rating = :locationRating, reviewed = 1, has_notification = 0 WHERE id = :id")
    suspend fun updateRating(locationRating: Int, id: Long)
}