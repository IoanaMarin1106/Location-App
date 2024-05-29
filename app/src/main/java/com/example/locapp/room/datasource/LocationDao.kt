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

//    @Query("UPDATE locations SET rating = :locationRating WHERE place_id = :placeId")
//    suspend fun updateRating(locationRating: Int, placeId: Int)
}