package com.example.locapp.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var hour: Int = 0,
    var day: Int = 0,
    var place_id: Int = 0,
    var rating: Int = 0,
    var used_for_training: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}