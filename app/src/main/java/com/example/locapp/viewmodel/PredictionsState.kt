package com.example.locapp.viewmodel

data class PredictionsState(
    val predictions: List<Pair<Int, Float>> = emptyList()
)
