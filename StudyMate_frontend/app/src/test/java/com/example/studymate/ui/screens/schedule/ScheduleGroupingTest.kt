package com.example.studymate.ui.screens.schedule

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleGroupingTest {
    @Test
    fun groupsTodayUpcomingAndPastDatesWrittenInSupportedLocales() {
        val today = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 30, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayStart = today.timeInMillis
        val frenchFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRENCH)
        val yesterday = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        val tomorrow = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }

        assertEquals(
            ScheduleGroup.PREVIOUS,
            schedule(frenchFormat.format(yesterday.time)).groupFor(todayStart)
        )
        assertEquals(
            ScheduleGroup.TODAY,
            schedule(frenchFormat.format(today.time)).groupFor(todayStart)
        )
        assertEquals(
            ScheduleGroup.UPCOMING,
            schedule(frenchFormat.format(tomorrow.time)).groupFor(todayStart)
        )
    }

    @Test
    fun removingSchedulesByIdsLeavesUnselectedSchedulesUntouched() {
        val schedules = listOf(
            schedule("28 Oct 2026").copy(id = 1),
            schedule("29 Oct 2026").copy(id = 2),
            schedule("30 Oct 2026").copy(id = 3)
        )

        assertEquals(listOf(2, 3), schedules.withoutIds(setOf(1)).map { it.id })
    }

    private fun schedule(date: String) = Schedule(
        id = 1,
        title = "Task",
        subject = "Tugas Sekolah",
        date = date,
        startTime = "",
        endTime = ""
    )
}
