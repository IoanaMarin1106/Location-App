package com.example.locapp.room.repository

import com.example.locapp.room.entity.Location
import com.example.locapp.room.datasource.LocationDao
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers.IO

interface Repository {

    suspend fun insert(locationEntity: Location)

    suspend fun getLastLocation(): List<Location>

    suspend fun getLastThreeLocations() : Flow<List<Int>>

    suspend fun getLocationsForTraining(): List<Location>
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

    override suspend fun getLastLocation(): List<Location> {
        return withContext(IO) {
            dao.getLastLocation()
        }
    }
}