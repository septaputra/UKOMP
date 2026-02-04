package com.example.studymate.ui.screens.schedule

data class Schedule(
    val id: Int,
    val title: String,
    val subject: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val note: String = "",
    val teacher: String = ""
)
