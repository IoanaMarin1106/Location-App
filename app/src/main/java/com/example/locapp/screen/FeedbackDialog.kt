package com.example.locapp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp


@Composable
fun FeedbackDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (Any?) -> Unit,
    dialogTitle: String,
    dialogText: AnnotatedString,
    icon: ImageVector,
) {
    var rating  by remember {
        mutableDoubleStateOf(0.0)
    }

    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Column {
                Text(text = dialogText)
                Spacer(modifier = Modifier.height(10.dp)) // Add spacing between text and rating bar
                RatingBar(
                    modifier = Modifier
                        .size(50.dp),
                    rating = rating,
                    onRatingChanged = {
                        rating = it
                    },
                    stars = 5,
                    starsColor = MaterialTheme.colorScheme.primary
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(rating)
                }
            ) {
                Text("Send feedback")
            }
        }
    )
}
