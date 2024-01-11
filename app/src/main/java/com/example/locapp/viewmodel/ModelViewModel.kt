package com.example.locapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locapp.network.ModelApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface ResponseState {
    data class Success(val response: String) : ResponseState
    object Error : ResponseState
    object Loading : ResponseState
}

class ModelViewModel: ViewModel() {

    var responseState: ResponseState by mutableStateOf(ResponseState.Loading)
        private set

    init {
        getModel()
    }

    fun getModel() {
        viewModelScope.launch {
            responseState = ResponseState.Loading
            responseState = try {
                val listResult = ModelApi.retrofitService.getModel()
                ResponseState.Success(
                    "Success: these are the new model url: ${listResult[0].data}"
                )
            } catch (e: IOException) {
                ResponseState.Error
            } catch (e: HttpException) {
                ResponseState.Error
            }
        }
    }
}