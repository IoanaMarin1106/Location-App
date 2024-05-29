package com.example.locapp.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.locapp.MainActivity
import com.example.locapp.collector.DataCollector
import com.example.locapp.receiver.RestartBackgroundService
import com.example.locapp.room.repository.Repository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.time.LocalDateTime
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class LocationService: Service() {
    var counter = 0
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    @Inject
    lateinit var repository: Repository

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    private val TAG = "LocationService"

    val dataCollector by lazy {
        DataCollector(repository = repository)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreateCommand has been called")
        createNotificationChanel()
        requestLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand has been called")
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroyCommand has been called")

        stopTimerTask()

        val broadcastIntent = Intent()
        broadcastIntent.action = "restart-service"
        broadcastIntent.setClass(this, RestartBackgroundService::class.java)

        this.sendBroadcast(broadcastIntent)
    }

    private fun createNotificationChanel() {
        val notificationChannelId = "com.getlocationbackground"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            notificationChannelId,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )

        chan.lightColor = Color.Cyan.toArgb()
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder =
            NotificationCompat.Builder(this, notificationChannelId)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running count::$counter")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    private fun startTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                val count = counter++
                if (latitude != 0.0 && longitude != 0.0) {
                    Log.d(TAG, "[Location]:: latitude: " + latitude.toString() + "::: longitude: " + longitude.toString() + " Count: " +
                                count.toString()
                    )
                }
            }
        }
        timer!!.schedule(
            timerTask,
            0,
            5000
        ) //1 * 60 * 1000 1 minute
    }

    private fun stopTimerTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        val client: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location: Location? = locationResult.lastLocation
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude

                        Log.d(TAG, "location update $location")

                        // collect location data

                        dataCollector.storeLocationData(baseContext, latitude, longitude, LocalDateTime.now())
                    }
                }
            }, null)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}