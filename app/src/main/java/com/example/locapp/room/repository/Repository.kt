package com.example.locapp.room.repository

import com.example.locapp.room.datasource.LocationDao
import com.example.locapp.room.entity.Location
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface Repository {

    suspend fun insert(locationEntity: Location)

    suspend fun getLastLocation(): List<Location>

    suspend fun getLastThreeLocations() : Flow<List<Int>>

    suspend fun getLocationsForTraining(): List<Location>

    suspend fun getLocationsWIthInProgressReview(): Flow<List<Location>>

    suspend fun getNotificationsCount(): Flow<Int>

    suspend fun updateNotificationIndicator(id: Long)
}

class RepositoryImpl @Inject constructor(
    private val dao: LocationDao,
) : Repository {
    override suspend fun insert(locationEntity: Location) {
        withContext(IO) {
            dao.insertLocation(locationEntity)
        }
    }

    override suspend fun getLastThreeLocations(): Flow<List<Int>> {
        return withContext(IO) {
            dao.getLastThreeEntries()
        }
    }

    override suspend fun getLocationsForTraining(): List<Location> {
        return withContext(IO) {
            dao.getLocationsForTraining()
        }
    }

    override suspend fun getLocationsWIthInProgressReview(): Flow<List<Location>> {
        return withContext(IO) {
            dao.getLocationsWithInProgressReview()
        }
    }

    override suspend fun getLastLocation(): List<Location> {
        return withContext(IO) {
            dao.getLastLocation()
        }
    }

    override suspend fun getNotificationsCount(): Flow<Int> {
        return withContext(IO) {
            dao.getNumberOfNotifications()
        }
    }

    override suspend fun updateNotificationIndicator(id: Long) {
        return withContext(IO) {
            dao.updateNotificationIndicator(id)
        }
    }
}