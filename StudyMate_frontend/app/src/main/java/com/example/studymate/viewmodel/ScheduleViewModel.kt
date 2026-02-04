package com.example.studymate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studymate.model.Schedule
import com.example.studymate.network.ApiClient
import kotlinx.coroutines.launch

// ViewModel responsible for fetching schedules and exposing UI state.
class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    // LiveData holding the list of schedules
    private val _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> = _schedules

    // LiveData holding a simple error message (nullable)
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // Public method to fetch schedules from the API using coroutines
    fun fetchSchedules() {
        viewModelScope.launch {
            try {
                // Make network call via ApiClient (suspend function)
                val response = ApiClient.apiService.getSchedules()

                if (response.isSuccessful) {
                    // Update LiveData with the response body or empty list
                    _schedules.postValue(response.body() ?: emptyList())
                    _error.postValue(null)
                } else {
                    // Simple error handling: expose HTTP status
                    _error.postValue("HTTP ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                // Network or parsing error
                _error.postValue(e.message ?: "Unknown error")
            }
        }
    }
}
