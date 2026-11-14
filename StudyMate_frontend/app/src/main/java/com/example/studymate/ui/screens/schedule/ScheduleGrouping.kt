package com.example.studymate.ui.screens.schedule

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class ScheduleGroup {
    PREVIOUS,
    TODAY,
    UPCOMING
}

fun scheduleDateMillis(date: String): Long? {
    val locales = (listOf(Locale.getDefault(), Locale.ENGLISH) +
        listOf("id", "en", "es", "fr", "pt", "de", "ja", "ko").map(Locale::forLanguageTag)).distinct()
    return locales.firstNotNullOfOrNull { locale ->
        runCatching {
            SimpleDateFormat("dd MMM yyyy", locale).apply { isLenient = false }.parse(date)?.let { parsed ->
                Calendar.getInstance().apply {
                    time = parsed
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
        }.getOrNull()
    }
}

fun startOfTodayMillis(): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

fun Schedule.groupFor(todayStartMillis: Long = startOfTodayMillis()): ScheduleGroup? {
    val dateMillis = scheduleDateMillis(date) ?: return null
    return when {
        dateMillis < todayStartMillis -> ScheduleGroup.PREVIOUS
        dateMillis > todayStartMillis -> ScheduleGroup.UPCOMING
        else -> ScheduleGroup.TODAY
    }
}

fun List<Schedule>.groupedFor(todayStartMillis: Long = startOfTodayMillis()): Map<ScheduleGroup, List<Schedule>> =
    groupBy { it.groupFor(todayStartMillis) }
        .mapNotNull { (group, schedules) -> group?.let { it to schedules } }
        .toMap()
        .mapValues { (_, schedules) ->
            schedules.sortedWith(
                when (schedules.firstOrNull()?.groupFor(todayStartMillis)) {
                    ScheduleGroup.PREVIOUS -> compareByDescending<Schedule> { scheduleDateMillis(it.date) }
                        .thenByDescending { it.startTime }
                    else -> compareBy<Schedule> { scheduleDateMillis(it.date) }
                        .thenBy { it.startTime }
                }
            )
        }
