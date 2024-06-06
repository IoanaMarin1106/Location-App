package com.example.locapp

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.locapp.collector.JsonReader
import com.example.locapp.collector.Place
import com.example.locapp.downloader.DownloadReceiver
import com.example.locapp.room.datasource.RoomDatabase
import com.example.locapp.room.entity.Location
import com.example.locapp.room.repository.Repository
import com.example.locapp.screen.ScreenHolder
import com.example.locapp.screen.SetUpNavGraph
import com.example.locapp.service.LocationService
import com.example.locapp.service.SocketService
import com.example.locapp.socket.SdkManager
import com.example.locapp.socket.TrainingDataProvider
import com.example.locapp.tflite.TFLiteModelManager
import com.example.locapp.ui.theme.LocAppTheme
import com.example.locapp.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: Repository

    private val utils: Utils = Utils()

    lateinit var navController: NavHostController

    companion object {
        private const val TAG = "MAIN ACTIVITY"

        lateinit var placeList: List<Place>
        lateinit var database: RoomDatabase

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SdkManager.setTrainingDataProvider(object : TrainingDataProvider {
            override fun getTrainingData(): Pair<MutableList<FloatArray>, MutableList<FloatArray>> {
                return getDataForTraining()
            }
        })

        SdkManager.startSocketService(this)

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



        // ---------------------  APPLICATION UI ------------------------------------
        setContent {
            LocAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // this allows the navigation between the application screens
                    navController = rememberNavController()

                    LaunchedEffect(Unit) {
                        if (intent?.getBooleanExtra("navigateToLocationReviews", false) == true) {
                            navController.navigate(ScreenHolder.LocationReviewsScreen.route)
                        }
                    }

                    SetUpNavGraph(navController = navController)
                }
            }
        }
    }

    private fun getDataForTraining(): Pair<MutableList<FloatArray>, MutableList<FloatArray>> {
        val features = mutableListOf<FloatArray>()
        val labels = mutableListOf<FloatArray>()

        var locations = emptyList<Location>()

        runBlocking {
            locations = repository.getLocationsForTrainingAndMarkAsUsed()
        }

        locations.forEach {
            features.add(floatArrayOf(it.hour.toFloat(), it.day.toFloat(), it.rating.toFloat()))

            val placeLabel = FloatArray(TFLiteModelManager.NUMBER_OF_KNOWN_PLACES) { 0f }
            placeLabel[it.place_id] = 1f
            labels.add(placeLabel)
        }

        Log.d("MainActivity", "Features: $features")
        Log.d("MainActivity", "Labels: $labels")

        return features to labels
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
