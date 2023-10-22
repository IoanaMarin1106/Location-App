package com.example.locapp

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.MutableBoolean
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.locapp.service.LocationService
import com.example.locapp.ui.theme.LocAppTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionsSafely(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        )

        setContent {
            LocAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LocationUpdateButton(context = LocalContext.current)
                }
            }
        }
    }

    private fun requestPermissionsSafely(
        permissions: Array<String>
    ) {
        requestPermissions(permissions!!, 200)
    }

    override fun onDestroy() {
        super.onDestroy()
        applicationContext.stopService(Intent(applicationContext, LocationService::class.java))
    }
}

@Composable
fun LocationUpdateButton(context: Context) {
    val serviceStatus = remember {
        mutableStateOf(false)
    }

    val buttonState = remember {
        mutableStateOf("Enable location updates")
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedButton(
            onClick = {

                if (serviceStatus.value) {
                    Toast.makeText(context, "Location updates disabled", Toast.LENGTH_SHORT).show()
                    serviceStatus.value = !serviceStatus.value
                    buttonState.value = "Enable location updates"
                    context.stopService(Intent(context, LocationService::class.java))
                } else {
                    // service not running start service.
                    Toast.makeText(context, "Location updates enabled", Toast.LENGTH_SHORT).show()
                    serviceStatus.value = !serviceStatus.value
                    buttonState.value = "Disable location updates"

                    // starting the service
                    context.startService(Intent(context, LocationService::class.java))
                }
            }
        ) {
            Text(
                text = buttonState.value,
                modifier = Modifier.padding(10.dp),
                color = Color.Cyan,
                fontSize = 20.sp
            )
        }
    }
}
