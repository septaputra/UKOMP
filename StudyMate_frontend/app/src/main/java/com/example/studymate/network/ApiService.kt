package com.example.studymate.network

import com.example.studymate.model.Schedule
import com.example.studymate.model.Note
import retrofit2.Response
import retrofit2.http.GET

// Retrofit interface defining API endpoints.
// Uses suspend functions so callers can use Kotlin coroutines.
interface ApiService {
    // GET /api/schedules -> returns a Response-wrapped list of Schedule objects
    @GET("schedules")
    suspend fun getSchedules(): Response<List<Schedule>>

    // GET /api/notes -> returns a list of Note objects directly.
    // Note: this endpoint returns the JSON array body and will throw
    // an exception for non-2xx responses which is handled by the ViewModel.
    @GET("notes")
    suspend fun getNotes(): List<Note>
}
