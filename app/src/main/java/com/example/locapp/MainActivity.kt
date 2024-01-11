package com.example.locapp

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.locapp.service.LocationService
import com.example.locapp.ui.screens.Home
import com.example.locapp.ui.screens.LocationUpdateButton
import com.example.locapp.ui.theme.LocAppTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionsSafely(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.INTERNET
            )
        )

        setContent {
            LocAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home(context = LocalContext.current)
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
        super.onDestroy()
        applicationContext.stopService(Intent(applicationContext, LocationService::class.java))
    }
}
