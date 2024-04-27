package com.example.locapp.collector

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.InputStream

class PlacesReader(private val context: Context) {

    fun readFoodieFootprintsFile(): String {
        val inputStream: InputStream = File(DataCollector.placesFilePath).inputStream()
        val inputString = inputStream.bufferedReader().use { it.readText() }
        return inputString
    }
}