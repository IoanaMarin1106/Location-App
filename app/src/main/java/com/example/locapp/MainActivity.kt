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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.locapp.collector.JsonReader
import com.example.locapp.collector.Place
import com.example.locapp.downloader.DownloadReceiver
import com.example.locapp.room.datasource.RoomDatabase
import com.example.locapp.screen.SetUpNavGraph
import com.example.locapp.service.LocationService
import com.example.locapp.service.SocketService
import com.example.locapp.ui.theme.LocAppTheme
import com.example.locapp.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val utils: Utils = Utils()

    lateinit var navController: NavHostController

    companion object {
        private const val TAG = "MAIN ACTIVITY"

        val checkpointsDirectoryPath = Environment.getExternalStorageDirectory().path + "/Download/Checkpoints"
        val modelDirectory = Environment.getExternalStorageDirectory().path + "/Download/Model"

        lateinit var placeList: List<Place>
        lateinit var database: RoomDatabase

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Example usage:
        placeList = JsonReader.readJsonFromAssets("places-mobile.json", this.applicationContext)

        Log.d(TAG, "On create")

        Log.d(TAG, "Request permissions")
        requestPermissionsSafely(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.POST_NOTIFICATIONS
            )
        )
        Log.d(TAG, "Permission has been added successfully")

        // create database
        Log.d(TAG, "Start database creation...")
        database = Room.databaseBuilder(
                applicationContext,
                RoomDatabase::class.java,
                "locations_database"
            ).fallbackToDestructiveMigration().build()

        // create checkpoints directory & model if not exist
        var result = utils.createDirectoryIfNotExists(checkpointsDirectoryPath)
        Log.d(TAG, "Checkpoints directory creation result: $result")

        result = utils.createDirectoryIfNotExists(modelDirectory)
        Log.d(TAG, "Model directory creation result: $result")


        // ---------------------  APPLICATION UI ------------------------------------
        setContent {
            LocAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // this allows the navigation between the application screens
                    navController = rememberNavController()
                    SetUpNavGraph(navController = navController)

                    LocalContext.current.apply {
                        startService(Intent(LocalContext.current, SocketService::class.java))
                    }
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
