package com.example.studymate.model

// Data class matching the JSON structure returned by the backend /api/notes
data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val subject: String,
    val created_at: String
)
