package com.example.studymate.ui.screens.notes

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

interface NotesRepository {
    val notes: StateFlow<List<Note>>

    fun getById(id: Int): Note?
    suspend fun add(title: String, content: String, isPinned: Boolean): Int
    suspend fun update(id: Int, title: String, content: String, isPinned: Boolean)
    suspend fun delete(id: Int)
    suspend fun togglePin(id: Int)
}

class LocalNotesRepository(context: Context) : NotesRepository {
    private val preferences = context.getSharedPreferences("study_notes", Context.MODE_PRIVATE)
    private val initialNotes = listOf(
        Note(1, "Jadwal Matematika", "Belajar integral jam 19.00", true, System.currentTimeMillis()),
        Note(2, "Tugas Fisika", "Kerjakan halaman 45", false, System.currentTimeMillis()),
        Note(3, "Catatan Biologi", "Fotosintesis & respirasi", false, System.currentTimeMillis()),
        Note(4, "Catatan Sejarah", "Perang dunia II ringkasan", false, System.currentTimeMillis()),
        Note(5, "Ide fitur StudyMate", "Tambahkan dark mode", false, System.currentTimeMillis())
    )
    private val _notes = MutableStateFlow(loadNotes())
    private var nextId = (_notes.value.maxOfOrNull { it.id } ?: 0) + 1
    override val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    override fun getById(id: Int): Note? = _notes.value.find { it.id == id }

    override suspend fun add(title: String, content: String, isPinned: Boolean): Int {
        val note = Note(nextId++, title, content, isPinned, System.currentTimeMillis())
        _notes.value = if (isPinned) listOf(note) + _notes.value else _notes.value + note
        persistNotes()
        return note.id
    }

    override suspend fun update(id: Int, title: String, content: String, isPinned: Boolean) {
        val updated = _notes.value.map { note ->
            if (note.id == id) {
                note.copy(title = title, content = content, isPinned = isPinned, updatedAt = System.currentTimeMillis())
            } else {
                note
            }
        }
        _notes.value = updated.sortedByDescending { it.isPinned }
        persistNotes()
    }

    override suspend fun delete(id: Int) {
        _notes.value = _notes.value.filterNot { it.id == id }
        persistNotes()
    }

    override suspend fun togglePin(id: Int) {
        _notes.value = _notes.value.map { note ->
            if (note.id == id) note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()) else note
        }.sortedByDescending { it.isPinned }
        persistNotes()
    }

    private fun loadNotes(): List<Note> {
        val json = preferences.getString("items", null) ?: return initialNotes
        return runCatching {
            val array = JSONArray(json)
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                Note(
                    id = item.getInt("id"),
                    title = item.getString("title"),
                    content = item.getString("content"),
                    isPinned = item.optBoolean("isPinned"),
                    updatedAt = item.optLong("updatedAt", System.currentTimeMillis())
                )
            }
        }.getOrElse { initialNotes }
    }

    private fun persistNotes() {
        preferences.edit().putString(
            "items",
            JSONArray().apply {
                _notes.value.forEach { note ->
                    put(JSONObject().apply {
                        put("id", note.id)
                        put("title", note.title)
                        put("content", note.content)
                        put("isPinned", note.isPinned)
                        put("updatedAt", note.updatedAt)
                    })
                }
            }.toString()
        ).apply()
    }
}