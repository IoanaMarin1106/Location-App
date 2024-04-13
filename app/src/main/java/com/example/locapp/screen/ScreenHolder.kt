package com.example.locapp.screen

sealed class ScreenHolder(val route: String) {
    object Home: ScreenHolder(route = "home_screen")
    object FoodieFootprints: ScreenHolder(route = "foodie_footprints_screen")
    object ForthcomingFavorites: ScreenHolder(route = "forthcoming_favorites_screen")
}
