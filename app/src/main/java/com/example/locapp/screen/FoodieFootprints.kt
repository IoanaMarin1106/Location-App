package com.example.locapp.screen


import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.locapp.R
import com.example.locapp.viewmodel.FoodieFootprintViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun FoodieFootprints(
    viewModel: FoodieFootprintViewModel,
    navController: NavController
) {
//    val places = viewModel.locationsStateFlow.value

    val loc1 = LatLng(44.4601146228763, 26.10057459989893)
    val loc2 = LatLng(44.43653280264863, 26.035486225039016)
    val loc3 = LatLng(44.43105136223451, 26.097028642227286)

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(loc1, 11f)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.padding(24.dp),
            text = "Let's see your last week resolutions...",
            color = Color.Black,
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 50.sp
        )

        // Map composable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                modifier = Modifier.fillMaxWidth()
            ) {
                MapMarker(
                    context = LocalContext.current,
                    position = loc1,
                    title = "VacaMUU",
                    iconSourceId = R.drawable.map
                )
                MapMarker(
                    context = LocalContext.current,
                    position = loc2,
                    title = "Roz Cafe",
                    iconSourceId = R.drawable.map
                )
                MapMarker(
                    context = LocalContext.current,
                    position = loc3,
                    title = "MOM",
                    iconSourceId = R.drawable.map
                )
            }
        }

        // Card with list
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.colorBackground)
            )
        ) {
            Column(modifier = Modifier
                .padding(16.dp)
                .align(CenterHorizontally)) {
                Text(
                    text = "#1 VacaMUU",
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "#2 Roz Cafe",
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "#3 MOM",
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // -------------   NEXT OPTIONS SECTION --------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .align(CenterHorizontally),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.colorBackground)
            )
        ) {
            Column(modifier = Modifier
                .padding(16.dp)
                .align(CenterHorizontally)) {
                Text(
                    text = "What are your next options?",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    textAlign = TextAlign.Center
                )

                Button(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.colorBackgroundDark)
                    ),
                    onClick = { navController.navigate(ScreenHolder.ForthcomingFavorites.route) }
                ) {
                    Text("START")
                }
            }
        }
    }
}

@Composable
fun MapMarker(
    context: Context,
    position: LatLng,
    title: String,
    @DrawableRes iconSourceId: Int
) {
    val icon = bitmapDescriptorFromVector(context, iconSourceId)

    Marker(
        state = MarkerState(position = position),
        title = title,
        icon = icon
    )
}

fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
) : BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

//@Composable
//@Preview(showBackground = true)
//fun FoodieFootprintsPreview() {
//    FoodieFootprints(
//        viewModel = FoodieFootprintsPreviewData(),
//        navController = rememberNavController()
//    )
//}