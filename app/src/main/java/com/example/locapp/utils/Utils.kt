package com.example.locapp.utils

import android.util.Log
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class Utils {
    private val TAG = "UTILS"

    fun createDirectoryIfNotExists(path: String) {
        val directory = File(path)
        if (directory.exists() && directory.isDirectory) {
            Log.d(TAG, "$path already exists.")
        } else {
            Log.d(TAG, "$path does not exists.")
            Files.createDirectory(Paths.get(path))
        }
    }

    fun getLastFileFromDirectory(path: String): File {
        val directory = File(path)

        if (!directory.exists() || !directory.isDirectory) {
            throw IllegalArgumentException("Invalid directory path: $path")
        }

        val files = directory.listFiles()
        if (files.isNullOrEmpty()) {
            throw NoSuchElementException("No files found in the directory: $path")
        }

        val lastModifiedFile = files.map {
            it.name
        }.maxOf { it }

        return File(path, lastModifiedFile)
    }
}