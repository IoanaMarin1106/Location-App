package com.example.locapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MachineLearningModel(
    val timestamp: String,
    @SerialName(value = "data")
    val data: String
)