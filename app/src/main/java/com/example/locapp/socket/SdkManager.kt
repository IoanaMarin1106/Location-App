package com.example.locapp.socket

import android.content.Context
import android.content.Intent
import com.example.locapp.service.SocketService
import com.example.locapp.tflite.TFLiteModelManager

object SdkManager {
    private var trainingDataProvider: TrainingDataProvider? = null
    private var modelManager: TFLiteModelManager? = null

    fun startSocketService(context: Context) {
        context.startService(Intent(context, SocketService::class.java))
    }

    fun setTrainingDataProvider(provider: TrainingDataProvider) {
        trainingDataProvider = provider
    }

    internal fun getTrainingDataProvider(): TrainingDataProvider? {
        return trainingDataProvider
    }

    fun setModelManager(manager: TFLiteModelManager) {
        modelManager = manager
    }

    fun predict(day: Int, hour: Int, rating: Int): Map<Int, Float>? {
        return modelManager?.predict(day, hour, rating)
    }
}