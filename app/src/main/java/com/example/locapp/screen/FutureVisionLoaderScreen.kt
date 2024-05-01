package com.example.locapp.screen

import android.os.Bundle
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.locapp.R
import com.example.locapp.viewmodel.PredictionsSharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun progressFlow(targetProgress: Float = 1f, step: Float = 0.007f, delayTime: Long = 1L): Flow<Float> {
    return flow {
        var progress = 0f
        while (progress <= targetProgress) {
            emit(progress)
            progress += step
            delay(delayTime)
        }
    }
}

@Composable
fun FutureVisionLoaderScreen(
    navController: NavHostController,
    predictionsSharedViewModel: PredictionsSharedViewModel = PredictionsSharedViewModel()
) {
    val progressFlow = remember { progressFlow(delayTime = 10L) }
    val progressState = progressFlow.collectAsState(initial = 0f)
    var showCompletionText by remember { mutableStateOf(false) }

    val predictions by predictionsSharedViewModel.predictionsState.collectAsState()

    LaunchedEffect(Unit) {
        progressFlow.collect { value ->
            if (value >= 0.99f) {
                delay(350)
                showCompletionText = true
            }
        }
    }

    Column {
        if (!showCompletionText) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .clickable(
                        onClick = { navController.navigate(ScreenHolder.ForthcomingFavorites.route) }
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = R.color.colorSecondary)
                    ))
                {
                    Text(
                        text = stringResource(id = R.string.future_vision_loader_prediction_waiting_message),
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(32.dp),
                        color = Color.White
                    )
                }

                Box (modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    CustomerCircularProgressBar(
                        progress = progressState.value,
                        progressArcColor1 = colorResource(id = R.color.colorSecondary),
                        progressArcColor2 = colorResource(id = R.color.colorSecondaryDark)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .clickable(
                        onClick = {
                            val places = predictions.predictions.map { it.first }.toIntArray()
                            val placeIds = places.joinToString(",")
                            navController.navigate("forthcoming_favorites_screen/${placeIds}")
                        }
                    ))
                {
                    Text(
                        text = stringResource(id = R.string.future_vision_loader_message),
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(32.dp),
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.globe),
                    contentDescription = "prediction",
                    modifier = Modifier.size(500.dp)
                )
            }
        }
    }
}

@Composable
fun CustomerCircularProgressBar(
    progress: Float = 0f,
    startAngle: Float = 270f,
    size: Dp = 96.dp,
    strokeWidth: Dp = 12.dp,
    backgroundArcColor: Color = Color.LightGray,
    progressArcColor1: Color = Color.Blue,
    progressArcColor2: Color = colorResource(id = R.color.colorBackgroundDark),
    animationOn: Boolean = false,
    animationDuration: Int = 1000
) {
    // Progress Animation Implementation
    val currentProgress = remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = currentProgress.floatValue,
        animationSpec = if (animationOn) tween(animationDuration) else tween(0),
        label = "Progress Animation"
    )

    LaunchedEffect(animationOn, progress) {
        if (animationOn) {
            progressFlow(progress).collect { value ->
                currentProgress.floatValue = value
            }
        } else {
            currentProgress.floatValue = progress
        }
    }

    Canvas(modifier = Modifier.size(size)) {
        val strokeWidthPx = strokeWidth.toPx()
        val arcSize = size.toPx() - strokeWidthPx

        val gradientBrush = Brush.verticalGradient(
            colors = listOf(progressArcColor1, progressArcColor2, progressArcColor1)
        )

        // Progress Arc Implementation
        withTransform({
            rotate(degrees = startAngle, pivot = center)
        }) {
            drawArc(
                brush = gradientBrush,
                startAngle = 0f,
                sweepAngle = animatedProgress * 360,
                useCenter = false,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )
        }
    }
}

@Preview
@Composable
fun CustomerCircularProgressBarPreview() {
    CustomerCircularProgressBar(
        progress = 0.85f,
        progressArcColor1 = Color(0xFF673AB7),
        progressArcColor2 = Color(0xFF4CAF50),
    )
}

//@Composable
//@Preview(showBackground = true)
//fun FutureVisionLoaderPreview() {
//    FutureVisionLoaderScreen(
//        navController = rememberNavController()
//    )
//}
