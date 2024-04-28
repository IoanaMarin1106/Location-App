package com.example.locapp.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetUpNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = ScreenHolder.Home.route
    ) {
        composable(route = ScreenHolder.Home.route) { HomeScreen(navController = navController, context = LocalContext.current) }
        composable(route = ScreenHolder.FoodieFootprints.route) { FoodieFootprints(navController = navController) }
        composable(route = ScreenHolder.ForthcomingFavorites.route) { ForthcomingFavoritesScreen(
            navController = navController
        )}
    }
}