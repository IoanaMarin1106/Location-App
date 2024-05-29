package com.example.locapp.room.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.locapp.room.datasource.LocationDao
import com.example.locapp.room.entity.Location

@Database(entities = [Location::class], version = 2)
abstract class RoomDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
}