package com.example.locapp.downloader

interface Downloader {
    fun downloadFile(url: String, destPath: String)
}