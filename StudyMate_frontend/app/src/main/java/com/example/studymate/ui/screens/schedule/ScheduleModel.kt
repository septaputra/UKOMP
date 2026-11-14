package com.example.studymate.ui.screens.schedule

data class Schedule(
    val id: Int,
    val title: String,
    val subject: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val note: String = "",
    val teacher: String = "",
    val isCompleted: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

internal fun List<Schedule>.withoutIds(ids: Set<Int>): List<Schedule> =
    filterNot { it.id in ids }

val scheduleCategories = listOf(
    "Belajar",
    "Sekolah",
    "Tugas Sekolah",
    "Olahraga",
    "Les",
    "Rumah"
)
