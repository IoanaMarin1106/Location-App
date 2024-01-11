package com.example.locapp.network

import com.example.locapp.model.MachineLearningModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL =
    "https://fed-learning-server-5nacwj4nsa-ew.a.run.app"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ModelApiService {

    @GET("global_model")
    suspend fun getModel(): List<MachineLearningModel>
}

object ModelApi {
    val retrofitService : ModelApiService by lazy {
        retrofit.create(ModelApiService::class.java)
    }
}

