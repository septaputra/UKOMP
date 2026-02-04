package com.example.studymate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studymate.model.Note
import com.example.studymate.network.ApiClient
import kotlinx.coroutines.launch

// ViewModel for Notes. Follows the same coroutine + LiveData pattern
// as ScheduleViewModel but calls ApiClient.apiService.getNotes().
class NotesViewModel(application: Application) : AndroidViewModel(application) {
    // Holds the list of notes fetched from the API
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    // Holds a simple error message (nullable)
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // Public method to fetch notes using coroutines
    fun fetchNotes() {
        viewModelScope.launch {
            try {
                // Calls the ApiService suspend function which returns List<Note>
                val result = ApiClient.apiService.getNotes()
                _notes.postValue(result)
                _error.postValue(null)
            } catch (e: Exception) {
                // Simple error handling: post the exception message
                _error.postValue(e.message ?: "Unknown error")
                _notes.postValue(emptyList())
            }
        }
    }
}
