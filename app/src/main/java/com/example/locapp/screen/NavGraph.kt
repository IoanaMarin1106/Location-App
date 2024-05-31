package com.example.locapp.screen

import ForthcomingFavoritesScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

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
        composable(
            route = "${ScreenHolder.ForthcomingFavorites.route}/{placeIds}",
            arguments = listOf(
                navArgument("placeIds") { type = NavType.StringType},
            )
        )
        { backStackEntry ->
            backStackEntry.arguments?.getString("placeIds")?.let {
                val places = it.split(",").map { i -> i.toInt() }.toIntArray()
                ForthcomingFavoritesScreen(navController = navController, placeIds = places)
            }
        }
        composable(
            route = ScreenHolder.FutureVisionLoader.route,
        ) {
            FutureVisionLoaderScreen(navController = navController, )
        }
        composable(route = ScreenHolder.LocationReviewsScreen.route) { LocationReviewsScreen(navController = navController) }
    }
}