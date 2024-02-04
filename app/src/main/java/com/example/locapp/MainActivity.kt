package com.example.locapp

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.locapp.downloader.DownloadReceiver
import com.example.locapp.service.LocationService
import com.example.locapp.service.SocketService
import com.example.locapp.ui.screens.LocationUpdateButton
import com.example.locapp.ui.theme.LocAppTheme
import com.example.locapp.utils.Utils

class MainActivity : ComponentActivity() {

    private val utils: Utils = Utils()
    companion object {
        private val TAG = "MAIN ACTIVITY"
        val checkpointsDirectoryPath = Environment.getExternalStorageDirectory().path + "/Download/Checkpoints"
        val modelDirectory = Environment.getExternalStorageDirectory().path + "/Download/Model"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "On create")

        // create checkpoints directory & model if not exist
        utils.createDirectoryIfNotExists(checkpointsDirectoryPath)
        utils.createDirectoryIfNotExists(modelDirectory)

        requestPermissionsSafely(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )

        setContent {
            LocAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LocalContext.current.apply {
                        startService(Intent(LocalContext.current, SocketService::class.java))
                    }

                    LocationUpdateButton(context = LocalContext.current)
                }
            }
        }
    }
    
    private fun requestPermissionsSafely(
        permissions: Array<String>
    ) {
        requestPermissions(permissions, 200)
    }

    override fun onDestroy() {
        Log.d(TAG, "On destroy")

        super.onDestroy()
        applicationContext.stopService(Intent(applicationContext, LocationService::class.java))
        applicationContext.stopService(Intent(applicationContext, SocketService::class.java))
        applicationContext.unregisterReceiver(DownloadReceiver())
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "On resume")
    }
}
