package com.example.studymate.ui.screens.main

import com.example.studymate.ui.screens.notes.Note
import com.example.studymate.ui.screens.schedule.Schedule
import java.text.SimpleDateFormat
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Locale

class LearningProgressMetricsTest {
    @Test
    fun countsSchoolTasksSeparatelyFromOtherLearningActivities() {
        val schedules = listOf(
            schedule(1, "Belajar", completed = true),
            schedule(2, "Tugas Sekolah", completed = true),
            schedule(3, "Tugas Sekolah", completed = false),
            schedule(4, "Olahraga", completed = true)
        )

        val metrics = LearningProgressMetrics.from(schedules, emptyList(), emptyList(), todayStart())

        assertEquals(4, metrics.totalActivities)
        assertEquals(4, metrics.eligibleSchedules)
        assertEquals(3, metrics.completedActivities)
        assertEquals(0, metrics.remainingActivities)
        assertEquals(2, metrics.schoolTaskCount)
        assertEquals(1, metrics.completedSchoolTasks)
        assertEquals(1, metrics.remainingSchoolTasks)
        assertEquals(50, metrics.schoolTaskCompletionPercent)
    }

    @Test
    fun countsPreviousTodayAndUpcomingSchedulesByCompletionStatus() {
        val schedules = buildList {
            for (offset in 1..5) {
                add(schedule(offset, "Belajar", completed = offset <= 3, dateOffset = -offset))
            }
            for (offset in 0..3) {
                add(schedule(6 + offset, "Olahraga", completed = offset == 0, dateOffset = 0))
            }
            for (offset in 1..6) {
                add(schedule(10 + offset, "Rumah", completed = offset == 1, dateOffset = offset))
            }
        }

        val metrics = LearningProgressMetrics.from(emptySchedulesWithDuplicates(schedules), emptyList(), emptyList(), todayStart())

        assertEquals(15, metrics.totalActivities)
        assertEquals(10, metrics.eligibleSchedules)
        assertEquals(5, metrics.completedActivities)
        assertEquals(2, metrics.remainingActivities)
    }

    @Test
    fun emptyTasksAndDuplicateSourcesDoNotInflateMetrics() {
        val notes = listOf(
            Note(1, "Catatan", "Isi", updatedAt = 1L),
            Note(1, "Catatan", "Isi", updatedAt = 2L)
        )
        val sessions = listOf(
            FocusSession("session-1", completedAtMillis = 10L, durationMillis = 25 * 60_000L),
            FocusSession("session-1", completedAtMillis = 10L, durationMillis = 25 * 60_000L)
        )

        val metrics = LearningProgressMetrics.from(emptyList(), notes, sessions)

        assertEquals(0, metrics.schoolTaskCount)
        assertEquals(0, metrics.completedSchoolTasks)
        assertEquals(0, metrics.schoolTaskCompletionPercent)
        assertEquals(1, metrics.noteCount)
        assertEquals(1, metrics.focusSessions)
        assertEquals(25, metrics.focusDurationMinutes)
    }

    private fun schedule(id: Int, category: String, completed: Boolean, dateOffset: Int = 0) = Schedule(
        id = id,
        title = "Aktivitas $id",
        subject = category,
        date = dateForOffset(dateOffset),
        startTime = "10:00",
        endTime = "11:00",
        isCompleted = completed
    )

    private fun todayStart(): Long = Calendar.getInstance().apply {
        set(2026, Calendar.OCTOBER, 30, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun dateForOffset(offset: Int): String = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(
        Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 30, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, offset)
        }.time
    )

    private fun emptySchedulesWithDuplicates(schedules: List<Schedule>) = schedules + schedules.first()
}
