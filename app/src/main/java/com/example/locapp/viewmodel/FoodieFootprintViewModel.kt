package com.example.locapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.locapp.collector.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FoodieFootprintViewModel: ViewModel() {
    private val _locationsStateFlow = MutableStateFlow<List<Place>>(emptyList())
    val locationsStateFlow = _locationsStateFlow.asStateFlow()

    init {
        val locations = listOf(
            Place(1, "VacaMuu",44.439667, 26.102500, 44.439668, 26.102501),
            Place(2, "Roz Cafe", 44.438211, 26.099903, 44.438212, 26.099904), // Set highlighted here
            Place(3, "MOM", 44.434866, 26.085928, 44.434867, 26.085929)
        )

        _locationsStateFlow.value = locations
    }

    fun getPlaces(): List<Place> = _locationsStateFlow.value.toList()
}