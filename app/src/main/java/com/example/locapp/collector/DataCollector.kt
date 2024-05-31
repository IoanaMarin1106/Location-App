package com.example.locapp.collector

import android.content.Context
import android.util.Log
import com.example.locapp.MainActivity
import com.example.locapp.notification.Notifier
import com.example.locapp.room.entity.Location
import com.example.locapp.room.repository.Repository
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

class DataCollector @Inject constructor(
    private val repository: Repository,
) {
    private val TAG = "DATA COLLECTOR"

    @OptIn(DelicateCoroutinesApi::class)
    fun storeLocationData(context: Context, latitude: Double, longitude: Double, timestamp: LocalDateTime) {
        Log.d(TAG, "Timestamp: $timestamp Lat: $latitude Long: $longitude")

        // get timestamp properties
        val dayOfWeek = timestamp.dayOfWeek.ordinal
        val hour = timestamp.hour

        // compute place id
        val placesList = computePlaceObject(context = context)
        Log.d(TAG, "Places list #: ${placesList.size}")

        val placeId = placesList.firstOrNull { it.isPointInPolygon(latitude, longitude) }?.placeId
        val rating = placesList.firstOrNull { it.isPointInPolygon(latitude, longitude) }?.rating

        // store the place
        if (placeId != null && rating != null) {
            GlobalScope.launch {

                val lastLoc = repository.getLastLocation().firstOrNull()

                if (placeId != lastLoc?.place_id || (hour != lastLoc.hour || dayOfWeek != lastLoc.day)) {
                    // insert current location into the database
                    repository.insert(Location(latitude, longitude, hour, dayOfWeek, placeId, rating))

                    // mark last location in the database that we need to send a notification for it
                    if (lastLoc != null) {
                        repository.updateNotificationIndicator(lastLoc.id)

                        val lastLocName = MainActivity.placeList.filter { (placeId) -> lastLoc.place_id ==  placeId }[0].name
                        Notifier().inviteToReview(context, lastLocName)
                    }
                }
            }
        }
    }

    private fun computePlaceObject(context: Context): MutableList<Place> {
        val fileName = "places-mobile.json"
        val placesList = mutableListOf<Place>()

        try {
            context.assets.open(fileName).bufferedReader().use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val place: Place = Gson().fromJson(line, Place::class.java)

                    placesList.add(place)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return placesList
    }
}