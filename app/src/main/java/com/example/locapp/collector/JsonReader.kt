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
                null
            }

            return place
        }

    }
}