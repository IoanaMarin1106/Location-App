package com.example.locapp.utils

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class Utils {

    companion object {
        private const val TAG = "UTILS"
    }

    fun createDirectoryIfNotExists(path: String): Boolean {
        val directory = File(path)
        return if (directory.exists() && directory.isDirectory) {
            Log.d(TAG, "$path already exists.")
            true
        } else {
            Log.d(TAG, "$path does not exists.")
            try {
                Files.createDirectory(Paths.get(path))
            } catch (e: IOException)
            {
                Log.d(TAG, "Cannot create directory $path.")
                false
            }
            true
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