package com.example.locapp.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.locapp.service.LocationService

@Composable
fun LocationUpdateButton(context: Context) {

    val serviceStatus = remember {
        mutableStateOf(false)
    }

    val buttonState = remember {
        mutableStateOf("Enable location updates")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedButton(
            onClick = {
                if (serviceStatus.value) {
                    Toast.makeText(context, "Location updates disabled", Toast.LENGTH_SHORT)
                        .show()
                    serviceStatus.value = !serviceStatus.value
                    buttonState.value = "Enable location updates"
                    context.stopService(Intent(context, LocationService::class.java))
                } else {
                    // service not running start service.
                    Toast.makeText(context, "Location updates enabled", Toast.LENGTH_SHORT)
                        .show()
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
                color = Color.Black,
                fontSize = 20.sp
            )
        }
    }
}