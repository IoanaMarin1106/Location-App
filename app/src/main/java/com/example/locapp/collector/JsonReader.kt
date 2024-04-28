package com.example.locapp.collector

import android.content.Context
import android.util.Log
import com.example.locapp.MainActivity
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStreamReader

class JsonReader {
    companion object {
        fun readJsonFromAssets(fileName: String, context: Context): List<Place> {
            val places = mutableListOf<Place>()
            try {
                val reader = BufferedReader(InputStreamReader(context.assets.open(fileName)) )
                val stringBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                    places.add(line?.let { parseJsonToPlace(it) }!!)
                }
                reader.close()
                stringBuilder.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            Log.d("TAG", places[0].name)
            return places
        }

        private fun parseJsonToPlace(jsonString: String) : Place? {
            val gson = Gson()
            val place: Place? = try {
                gson.fromJson(jsonString, Place::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null // Handle parsing exceptions (optional)
            }

            if (place != null) {
                // Use the parsed Place object
                println("Place ID: ${place.placeId}")
                println("Name: ${place.name}")
                // ... and so on
            } else {
                // Handle parsing errors (optional)
                println("Error parsing JSON!")
            }

            return place
        }

        fun readFoodieFootprints(): List<String> {
            val ids = mutableListOf<String>()
            var reader: BufferedReader? = null

            try {
                reader = BufferedReader(FileReader(MainActivity.locationDataFilePath))
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    println(line)
                    ids.add(line!!.split(',')[4])
                }
            } catch (e: Exception) {
                println("An error occurred: ${e.message}")
            } finally {
                try {
                    reader?.close()
                } catch (e: Exception) {
                    println("An error occurred while closing the file: ${e.message}")
                }
            }

            Log.d("TAG", ids[0])
            return ids
        }
    }
}