package com.example.locapp.collector

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime

class DataCollector {
    private val TAG = "DATA COLLECTOR"

    companion object {
        val placesFilePath = Environment.getExternalStorageDirectory().path + "/Download/LocationData/locations.txt"
    }

    private var lastLocation: Triple<Int, Int, Int> = if (File(placesFilePath).exists()) {
        val content = File(placesFilePath).readLines()

        val lastLine = content.lastOrNull()
        val (_, _, hour, dayOfWeek, lastPlaceId) = lastLine!!.split(',')

        Triple(hour.toInt(), dayOfWeek.toInt(), lastPlaceId.toInt())
    } else {
        Triple(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
    }

    fun refreshLastLocation() {
        lastLocation = Triple(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
    }

    fun storeLocationData(context: Context, latitude: Double, longitude: Double, timestamp: LocalDateTime) {
        Log.d(TAG, "Timestamp: $timestamp Lat: $latitude Long: $longitude")

        // get timestamp properties
        val dayOfWeek = timestamp.dayOfWeek.ordinal
        val hour = timestamp.hour

        // compute place id
        val placesList = computePlaceObject(context = context)
        Log.d(TAG, "Places list #: ${placesList.size}")

        val placeId = placesList.firstOrNull { it.isPointInPolygon(latitude, longitude) }?.placeId

        // store the place
        if (placeId != null) {
            if (placeId != lastLocation.third || (hour != lastLocation.first || dayOfWeek != lastLocation.second)) {
                lastLocation = Triple(hour, dayOfWeek, placeId)
                append(latitude.toString(), longitude.toString(), hour.toString(), dayOfWeek.toString(), placeId.toString())
            }
        }
    }

    private fun append(lat: String, long: String, hour: String, dayOfWeek: String, placeId: String)
    {
        // TODO: modify type of file to .loc

        // Data format: lat, long, hh, day of week, place-id
        val file = File(placesFilePath)

        // If the parent directory doesn't exist, create it along with the file
        if (file.parentFile?.exists() == false) {
            file.parentFile?.mkdirs()
        }

        // If the file doesn't exist, create a new one
        if (!file.exists()) {
            file.createNewFile()
        }

        val data = "$lat,$long,$hour,$dayOfWeek,$placeId"

        try {
            val fileWriter = FileWriter(file, true)
            val bufferedWriter = BufferedWriter(fileWriter)

            // Write data to the file
            bufferedWriter.write(data)
            bufferedWriter.newLine()

            // Close the BufferedWriter
            bufferedWriter.close()

            Log.d(TAG, "Data [$data] was appended to the file")
        } catch (e: IOException) {
            e.printStackTrace()
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

                    Log.d(TAG, "Deserialized Place Object: $place")
                    placesList.add(place)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return placesList
    }
}