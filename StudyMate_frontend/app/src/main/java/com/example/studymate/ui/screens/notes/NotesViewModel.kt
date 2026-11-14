package com.example.studymate.ui.screens.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotesRepository = LocalNotesRepository(application)
    val fullNotes: StateFlow<List<Note>> = repository.notes

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val filteredNotes: StateFlow<List<Note>> = combine(fullNotes, _query) { list, q ->
        if (q.isBlank()) list else list.filter { n ->
            n.title.contains(q, ignoreCase = true) || n.content.contains(q, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, fullNotes.value)

    fun setQuery(q: String) {
        _query.value = q
    }

    fun addNote(title: String, content: String, isPinned: Boolean) {
        viewModelScope.launch { repository.add(title, content, isPinned) }
    }

    suspend fun saveDraft(
        id: Int?,
        title: String,
        content: String,
        isPinned: Boolean
    ): Int {
        val normalizedTitle = title.trim()
        val normalizedContent = content.trim()
        return if (id == null) {
            repository.add(normalizedTitle, normalizedContent, isPinned)
        } else {
            repository.update(id, normalizedTitle, normalizedContent, isPinned)
            id
        }
    }

    fun updateNote(id: Int, title: String, content: String, isPinned: Boolean) {
        viewModelScope.launch { repository.update(id, title, content, isPinned) }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch { repository.delete(id) }
    }

    fun togglePin(id: Int) {
        viewModelScope.launch { repository.togglePin(id) }
    }

    fun setPinned(ids: Set<Int>, pinned: Boolean) {
        viewModelScope.launch {
            ids.forEach { id ->
                val note = repository.getById(id)
                if (note != null && note.isPinned != pinned) {
                    repository.togglePin(id)
                }
            }
        }
    }

    fun deleteNotes(ids: Set<Int>) {
        viewModelScope.launch {
            ids.forEach { id -> repository.delete(id) }
        }
    }

    fun getNoteById(id: Int): Note? = repository.getById(id)
}
