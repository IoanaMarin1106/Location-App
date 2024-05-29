package com.example.locapp.screen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.locapp.R
import com.example.locapp.service.LocationService

@Composable
fun HomeScreen(
    context: Context,
    navController: NavHostController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add the background image
        Image(
            painter = painterResource(id = R.drawable.home_map),
            contentDescription = "Background image"
        )

        Text(
            modifier = Modifier
                .padding(36.dp),
            textAlign = TextAlign.Center,
            text = "Let's Put You on the Map! (Literally)",
            color = colorResource(id = R.color.colorSecondary),
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            fontWeight = FontWeight.Bold
        )

        Button(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.colorSecondaryDark)
            ),
            onClick = {
                // starting the service
                context.startService(Intent(context, LocationService::class.java))

                // navigate to the second screen when at least 3 locations were collected
                navController.navigate(route = ScreenHolder.FoodieFootprints.route)
            }
        ) {
            Text("Engage Location Tracking Protocol")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(
        context = LocalContext.current,
        navController = rememberNavController()
    )
}