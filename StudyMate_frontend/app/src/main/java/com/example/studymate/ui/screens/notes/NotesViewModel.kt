package com.example.studymate.ui.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {
    private var nextId = 6

    private val seed = listOf(
        Note(1, "Jadwal Matematika", "Belajar integral jam 19.00", true, System.currentTimeMillis()),
        Note(2, "Tugas Fisika", "Kerjakan halaman 45", false, System.currentTimeMillis()),
        Note(3, "Catatan Biologi", "Fotosintesis & respirasi", false, System.currentTimeMillis()),
        Note(4, "Catatan Sejarah", "Perang dunia II ringkasan", false, System.currentTimeMillis()),
        Note(5, "Ide fitur StudyMate", "Tambahkan dark mode", false, System.currentTimeMillis())
    )

    private val _fullNotes = MutableStateFlow(seed)
    val fullNotes: StateFlow<List<Note>> = _fullNotes.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val filteredNotes: StateFlow<List<Note>> = combine(_fullNotes, _query) { list, q ->
        if (q.isBlank()) list else list.filter { n ->
            n.title.contains(q, ignoreCase = true) || n.content.contains(q, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _fullNotes.value)

    fun setQuery(q: String) {
        _query.value = q
    }

    fun addNote(title: String, content: String, isPinned: Boolean) {
        viewModelScope.launch {
            val note = Note(nextId++, title, content, isPinned, System.currentTimeMillis())
            val current = _fullNotes.value.toMutableList()
            if (isPinned) current.add(0, note) else current.add(note)
            _fullNotes.value = current
        }
    }

    fun updateNote(id: Int, title: String, content: String, isPinned: Boolean) {
        viewModelScope.launch {
            val current = _fullNotes.value.toMutableList()
            val idx = current.indexOfFirst { it.id == id }
            if (idx != -1) {
                val updated = current[idx].copy(title = title, content = content, isPinned = isPinned, updatedAt = System.currentTimeMillis())
                current.removeAt(idx)
                if (isPinned) current.add(0, updated) else current.add(updated)
                _fullNotes.value = current
            }
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            _fullNotes.value = _fullNotes.value.filterNot { it.id == id }
        }
    }

    fun togglePin(id: Int) {
        viewModelScope.launch {
            val current = _fullNotes.value.toMutableList()
            val idx = current.indexOfFirst { it.id == id }
            if (idx != -1) {
                val n = current[idx].copy(isPinned = !current[idx].isPinned, updatedAt = System.currentTimeMillis())
                current.removeAt(idx)
                if (n.isPinned) current.add(0, n) else current.add(n)
                _fullNotes.value = current
            }
        }
    }

    fun getNoteById(id: Int): Note? = _fullNotes.value.find { it.id == id }
}
