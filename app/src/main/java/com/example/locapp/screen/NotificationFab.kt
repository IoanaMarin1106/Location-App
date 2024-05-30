package com.example.locapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.locapp.R
import com.example.locapp.viewmodel.NotificationFabViewModel

@Composable
fun NotificationFab(
    navController: NavHostController,
) {
    val notificationFabViewModel = hiltViewModel<NotificationFabViewModel>()

    LaunchedEffect(key1 = true, block = {
        notificationFabViewModel.getNotificationsCount()
    })

    val notificationCount by notificationFabViewModel.notificationNumber.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        FloatingActionButton(
            onClick = {
                navController.navigate(ScreenHolder.LocationReviewsScreen.route)
            },

            containerColor = colorResource(id = R.color.colorBackgroundDark),
            modifier = Modifier
                .padding(16.dp)
                .size(56.dp)
        ) {
            Box {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                )
                if (notificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .offset(14.dp, (-14).dp) // Adjust these values as needed
                            .background(Color.Red, shape = MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notificationCount.toString(),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    NotificationFab(navController = rememberNavController())
}