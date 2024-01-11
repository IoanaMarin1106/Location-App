package com.example.locapp.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locapp.R
import com.example.locapp.model.MachineLearningModel
import com.example.locapp.service.LocationService
import com.example.locapp.viewmodel.ModelViewModel
import com.example.locapp.viewmodel.ResponseState

@Composable
fun Home(context: Context) {
    val modelViewModel: ModelViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        //LocationUpdateButton(context = context)
        HomeScreen(responseState = modelViewModel.responseState)
    }
}

@Composable
fun HomeScreen(
    responseState: ResponseState,
    modifier: Modifier = Modifier
) {
    when (responseState) {
        is ResponseState.Loading -> LoadingScreen(modifier = modifier.fillMaxWidth())
        is ResponseState.Success -> ResultScreen(
            responseState.response, modifier = modifier.fillMaxWidth()
        )
        is ResponseState.Error -> ErrorScreen(modifier = modifier.fillMaxWidth())
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun ResultScreen(url: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(text = url)
    }
}

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