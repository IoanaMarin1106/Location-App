package com.example.locapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locapp.room.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationFabViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _notificationsNumber = MutableStateFlow(0)
    val notificationNumber = _notificationsNumber.asStateFlow()

    fun getNotificationsCount() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getNotificationsCount().collectLatest {
                _notificationsNumber.tryEmit(it)
            }
        }
    }

}