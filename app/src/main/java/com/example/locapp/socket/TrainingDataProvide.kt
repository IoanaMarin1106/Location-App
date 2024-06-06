package com.example.locapp.socket

interface TrainingDataProvider {
    fun getTrainingData(): Pair<MutableList<FloatArray>, MutableList<FloatArray>>
}