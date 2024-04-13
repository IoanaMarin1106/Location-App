package com.example.locapp.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.locapp.R

@Composable
fun ForthcomingFavoritesScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Map composable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            // Add your map composable here (e.g., GoogleMap, etc.)
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
                containerColor = colorResource(id = R.color.colorBackground)
            )
        ) {
            Column(modifier = Modifier
                .padding(16.dp)
                .align(CenterHorizontally)
            ) {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(CenterHorizontally)
                        .verticalScroll(state = rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(CenterHorizontally),
                    ) {
                        Text(
                            text = "#1 Pub 18",
                            textAlign = TextAlign.Center,
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "#2 balls.",
                            textAlign = TextAlign.Center,
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "#3 AveForchetta",
                            textAlign = TextAlign.Center,
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.colorBackgroundDark)
                    ),
                    onClick = {  }
                ) {
                    Text("More")
                }
            }

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
@Preview(showBackground = true)
fun ForthcomingFavoritesPreview() {
    ForthcomingFavoritesScreen(
        navController = rememberNavController()
    )
}