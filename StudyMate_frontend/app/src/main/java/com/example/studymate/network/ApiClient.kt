package com.example.studymate.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton object providing a configured Retrofit instance and ApiService
object ApiClient {
    // BASE_URL must use Android emulator special host to reach host machine
    // Note: do NOT use localhost or 127.0.0.1 here. Use 10.0.2.2
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Expose the ApiService for the app to use
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
