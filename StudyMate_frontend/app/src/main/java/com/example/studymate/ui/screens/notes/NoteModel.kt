package com.example.studymate.ui.screens.notes

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
