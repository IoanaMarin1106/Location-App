package com.example.locapp.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

class AndroidDownloader(
    private val context: Context
): Downloader {


    override fun downloadFile(url: String, destPath: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, destPath)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }
}