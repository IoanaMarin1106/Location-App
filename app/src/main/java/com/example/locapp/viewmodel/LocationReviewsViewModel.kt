package com.example.locapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locapp.room.entity.Location
import com.example.locapp.room.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationReviewsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _locationsList = MutableStateFlow(emptyList<Location>())
    val locationsList = _locationsList.asStateFlow()

    fun getLocationsForReview() {
        viewModelScope.launch(IO) {
            repository.getLocationsWIthInProgressReview().collectLatest {
                _locationsList.tryEmit(it)
            }
        }
    }

}