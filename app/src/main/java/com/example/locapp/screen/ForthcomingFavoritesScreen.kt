package com.example.locapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
import com.example.locapp.MainActivity
import com.example.locapp.R
import com.example.locapp.collector.LocationInfo
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ForthcomingFavoritesScreen(
    navController: NavHostController,
    placeIds: IntArray,
) {
    val locationsList = MainActivity.placeList.filter {
        placeIds.contains(it.placeId)
    }.map {
        LocationInfo(it.name, LatLng((it.nLatitude + it.sLatitude) / 2, (it.wLongitude + it.eLongitude) / 2))
    }.toMutableList()

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(locationsList[0].latLong, 11f)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Map composable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                modifier = Modifier.fillMaxWidth()
            ) {
                locationsList.forEach { location ->
                    MapMarker(
                        context = LocalContext.current,
                        position = location.latLong,
                        title = location.name,
                        iconSourceId = R.drawable.pin
                    )
                }
            }
        }

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp))

        // Card with list
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.colorSecondary)
            )
        ) {
            LocationList(locationsList = locationsList)
        }

        Button(
            modifier = Modifier
                .padding(16.dp)
                .align(CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.colorBackgroundDark)
            ),
            onClick = { navController.navigate(ScreenHolder.FoodieFootprints.route) {
                    popUpTo(ScreenHolder.FoodieFootprints.route)
                }
            }
        ) {
            Text("Go Home")
        }
    }
}

@Composable
fun LocationList(locationsList: MutableList<LocationInfo>) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .height(200.dp)
            .verticalScroll(state = rememberScrollState())
    ) {
        val displayedLocations = if (expanded) {
            locationsList
        } else {
            locationsList.take(3)
        }

        displayedLocations.forEach { location ->
            Row {
                Image(
                    painter = painterResource(id = R.drawable.pin),
                    contentDescription = location.name,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = location.name,
                    textAlign = TextAlign.Left,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (!expanded && locationsList.size > 3) {
            Button(
                modifier = Modifier.padding(16.dp).align(CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.colorBackgroundDark)
                ),
                onClick = { expanded = true },
            ) {
                Text("Show More")
            }
        } else if (expanded) {
            Button(
                modifier = Modifier.padding(16.dp).align(CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.colorBackgroundDark)
                ),
                onClick = { expanded = false },
            ) {
                Text("Show Less")
            }
        }
    }
}


//@Composable
//@Preview(showBackground = true)
//fun ForthcomingFavoritesPreview() {
//    ForthcomingFavoritesScreen(
//        navController = rememberNavController(),
//
//    )
//}