package com.example.locapp.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.locapp.MainActivity
import com.example.locapp.R
import com.example.locapp.room.entity.Location
import com.example.locapp.viewmodel.LocationReviewsViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationReviewsScreen(
    navController: NavHostController,
) {
    val locationReviewsViewModel = hiltViewModel<LocationReviewsViewModel>()

    LaunchedEffect(key1 = true, block = {
        locationReviewsViewModel.getLocationsForReview()
    })

    val locations by locationReviewsViewModel.locationsList.collectAsStateWithLifecycle()
    Log.d("HEHE", locations.isEmpty().toString())

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (locations.isNotEmpty()) {
                        Text(
                            text = "Location Reviews",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            })
        },
        floatingActionButton = {
            BackFab(navController = navController)
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        if (locations.isEmpty()) {
            Column(modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "No review needed",
                    style = MaterialTheme.typography.titleLarge
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.checkmark),
                    contentDescription = "Leave a Review",
                    tint = colorResource(id = R.color.colorSecondary),
                    modifier = Modifier.size(200.dp)
                )
            }
        } else {
            Column(modifier = Modifier.padding(padding)) {
                locations.forEach { location ->
                    LocationReviewItem(location = location, locationReviewsViewModel)
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LocationReviewItem(location: Location, locationReviewsViewModel: LocationReviewsViewModel) {
    val place = MainActivity.placeList.firstOrNull { it.placeId == location.place_id }

    var isFeedbackDialogDisplayed by remember {
        mutableStateOf(false)
    }

    if (isFeedbackDialogDisplayed) {
        val dialogText = buildAnnotatedString {
            append("On a scale of \"")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(place!!.name)
            }
            append(" is magical!\" to\n" +
                    "\"I'd rather eat a sock,\" how was your experience?")
        }

        FeedbackDialog(
            onDismissRequest = { isFeedbackDialogDisplayed = false },
            onConfirmation = { rating ->
                GlobalScope.launch {
                    locationReviewsViewModel.updateRatingAndRemoveLocation((rating as Double).toInt(), location)
                    isFeedbackDialogDisplayed = false
                }
            },
            dialogTitle = "On a scale of...",
            dialogText = dialogText,
            icon = Icons.Default.Star
        )
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = {
            isFeedbackDialogDisplayed = true
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side icon
            Image(
                painter = painterResource(id = R.drawable.pin),
                contentDescription = place?.name,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            // Order details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (place != null) {
                    Text(text = place.name, style = MaterialTheme.typography.titleLarge)
                    Text(text = location.timestamp.toPrettyDateTime(), style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Review invitation
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 16.dp)
            ) {

                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.star_empty_svgrepo_com),
                        contentDescription = "Leave a Review",
                        tint = Color.Yellow,
                        modifier = Modifier.size(40.dp)
                    )

                Text(
                    text = AnnotatedString("Leave a Review"),
                    style = MaterialTheme.typography.bodyMedium.copy(color = colorResource(id = R.color.colorSecondary))
                )
            }
        }
    }
}

@Composable
fun BackFab(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
        FloatingActionButton(
            containerColor = colorResource(id = R.color.colorSecondary),
            modifier = Modifier
                .padding(16.dp)
                .size(56.dp),
            onClick = { navController.navigate(ScreenHolder.FoodieFootprints.route) },
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LocationsReviewScreenPreview() {
    LocationReviewsScreen(navController = rememberNavController())
}

fun Long.toPrettyDateTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(this))
}