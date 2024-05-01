package com.example.locapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locapp.tflite.TFLiteModelManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PredictionsSharedViewModel: ViewModel() {
    private val _modelManager = TFLiteModelManager()

    private val _predictionsState = MutableStateFlow(PredictionsState())
    val predictionsState: StateFlow<PredictionsState> = _predictionsState.asStateFlow()

    init {
        predict()
    }

    fun predict() {
        viewModelScope.launch {
            val predictions = makePrediction()
            val sortedPredictions = predictions.toList().sortedByDescending { it.second }
            val top10Predictions = sortedPredictions.take(10)
            _predictionsState.value = PredictionsState(top10Predictions)
        }
    }

    // predict the future places as a map of <id_place, probability_score>
    private suspend fun makePrediction(): Map<Int, Float> {
        val currentLocalDate = LocalDateTime.now()
        val hour = currentLocalDate.hour
        val day = currentLocalDate.dayOfWeek.ordinal

        return _modelManager.predict(day, hour)
    }
}