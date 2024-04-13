package com.example.locapp.screen


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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.locapp.R
import com.google.maps.android.compose.GoogleMap

@Composable
fun FoodieFootprints(
    navController: NavController
) {

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
            GoogleMap(modifier = Modifier.fillMaxWidth())
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
@Preview(showBackground = true)
fun FoodieFootprintsPreview() {
    FoodieFootprints(
        navController = rememberNavController()
    )
}