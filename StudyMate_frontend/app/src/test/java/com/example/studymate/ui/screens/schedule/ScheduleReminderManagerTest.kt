package com.example.studymate.ui.screens.schedule

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleReminderManagerTest {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    @Test
    fun schoolTaskUsesLocalDailyReminderTimesUntilItsDeadline() {
        val now = localTime(2026, Calendar.OCTOBER, 27, 7)
        val task = schedule(
            category = "Tugas Sekolah",
            deadline = dateFormat.format(localTime(2026, Calendar.OCTOBER, 29, 0)),
            createdAt = now
        )

        assertEquals(localTime(2026, Calendar.OCTOBER, 27, 8), ScheduleReminderManager.nextReminderTime(task, now))
        assertEquals(
            localTime(2026, Calendar.OCTOBER, 27, 13),
            ScheduleReminderManager.nextReminderTime(task, localTime(2026, Calendar.OCTOBER, 27, 8))
        )
        assertEquals(
            localTime(2026, Calendar.OCTOBER, 28, 8),
            ScheduleReminderManager.nextReminderTime(task, localTime(2026, Calendar.OCTOBER, 27, 19))
        )
    }

    @Test
    fun completedOrPastDeadlineTasksDoNotReceiveNewReminders() {
        val now = localTime(2026, Calendar.OCTOBER, 30, 7)
        val task = schedule(
            category = "Tugas Sekolah",
            deadline = dateFormat.format(localTime(2026, Calendar.OCTOBER, 29, 0)),
            createdAt = localTime(2026, Calendar.OCTOBER, 27, 7)
        )

        assertNull(ScheduleReminderManager.nextReminderTime(task, now))
        assertNull(ScheduleReminderManager.nextReminderTime(task.copy(isCompleted = true), now))
    }

    @Test
    fun schoolTaskWithoutTimesStillReceivesDailyRemindersUntilItsDeadline() {
        val now = localTime(2026, Calendar.OCTOBER, 27, 7)
        val task = schedule(
            category = "Tugas Sekolah",
            deadline = dateFormat.format(localTime(2026, Calendar.OCTOBER, 27, 0)),
            createdAt = now,
            startTime = ""
        ).copy(endTime = "")

        assertEquals(
            localTime(2026, Calendar.OCTOBER, 27, 8),
            ScheduleReminderManager.nextReminderTime(task, now)
        )
    }

    @Test
    fun regularScheduleReminderUsesItsStartTimeAndSkipsPastSchedules() {
        val now = localTime(2026, Calendar.OCTOBER, 27, 7)
        val future = schedule(
            category = "Belajar",
            deadline = dateFormat.format(localTime(2026, Calendar.OCTOBER, 28, 0)),
            createdAt = now,
            startTime = "09:30"
        )
        val past = future.copy(date = dateFormat.format(localTime(2026, Calendar.OCTOBER, 26, 0)))

        assertEquals(
            localTime(2026, Calendar.OCTOBER, 28, 9, 30),
            ScheduleReminderManager.nextReminderTime(future, now)
        )
        assertNull(ScheduleReminderManager.nextReminderTime(past, now))
    }

    private fun schedule(
        category: String,
        deadline: String,
        createdAt: Long,
        startTime: String = "09:00"
    ) = Schedule(
        id = 1,
        title = "Belajar",
        subject = category,
        date = deadline,
        startTime = startTime,
        endTime = "10:00",
        createdAtMillis = createdAt
    )

    private fun localTime(year: Int, month: Int, day: Int, hour: Int, minute: Int = 0): Long =
        Calendar.getInstance().apply {
            set(year, month, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
}
