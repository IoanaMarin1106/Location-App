package com.example.locapp.downloader

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log

class AndroidDownloader(
    private val context: Context
): Downloader {


    override fun downloadFile(url: String, destPath: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, destPath)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        // Register your BroadcastReceiver to receive the download completion broadcast
        val downloadReceiver = DownloadReceiver()
        context.registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        val downloadId = downloadManager.enqueue(request)
        Log.d("IOANA", downloadId.toString())
    }
}