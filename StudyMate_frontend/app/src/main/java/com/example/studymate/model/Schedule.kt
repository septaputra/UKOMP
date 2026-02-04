package com.example.studymate.model

// Data class representing a schedule item returned by the API
data class Schedule(
    val subject: String,
    val day: String,
    val time: String,
    val note: String
)
