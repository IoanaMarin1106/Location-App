package com.example.locapp.collector

import com.google.gson.annotations.SerializedName

data class Place(
    @SerializedName("place_id")
    val placeId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("s_latitude")
    val sLatitude: Double,
    @SerializedName("n_latitude")
    val nLatitude: Double,
    @SerializedName("w_longitude")
    val wLongitude: Double,
    @SerializedName("e_longitude")
    val eLongitude: Double
)

fun Place.isPointInPolygon(latitude: Double, longitude: Double): Boolean =
    latitude in sLatitude..nLatitude && longitude in wLongitude ..eLongitude