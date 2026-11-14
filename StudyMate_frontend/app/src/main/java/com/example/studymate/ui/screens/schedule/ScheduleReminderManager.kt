package com.example.studymate.ui.screens.schedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

internal object ScheduleNotificationNavigation {
    const val EXTRA_SCHEDULE_ID = "schedule_notification_id"
    private val _scheduleId = MutableStateFlow<Int?>(null)
    val scheduleId = _scheduleId.asStateFlow()

    fun publish(id: Int) {
        _scheduleId.value = id
    }

    fun consume() {
        _scheduleId.value = null
    }
}

internal object ScheduleReminderManager {
    const val ACTION_REMIND = "com.example.studymate.action.SCHEDULE_REMINDER"
    const val ACTION_COMPLETE = "com.example.studymate.action.COMPLETE_SCHEDULE"
    const val ACTION_OPEN = "com.example.studymate.action.OPEN_SCHEDULE"
    const val EXTRA_EXPECTED_AT = "expected_reminder_at"
    private const val REQUEST_OFFSET_REMINDER = 1_000_000
    private const val REQUEST_OFFSET_COMPLETE = 2_000_000
    private const val REQUEST_OFFSET_OPEN = 3_000_000
    fun syncAll(context: Context, schedules: List<Schedule>) {
        schedules.forEach { cancel(context, it.id) }
        schedules.forEach { schedule(context, it) }
    }

    fun schedule(context: Context, schedule: Schedule, nowMillis: Long = System.currentTimeMillis()) {
        if (schedule.isCompleted) {
            cancel(context, schedule.id)
            return
        }
        val reminderAt = nextReminderTime(schedule, nowMillis) ?: run {
            cancel(context, schedule.id)
            return
        }
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pendingIntent = reminderPendingIntent(context, schedule.id, reminderAt)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderAt, pendingIntent)
    }

    fun cancel(context: Context, scheduleId: Int) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(reminderPendingIntent(context, scheduleId, 0L))
    }

    fun nextReminderTime(schedule: Schedule, nowMillis: Long): Long? {
        if (schedule.isCompleted) return null
        val scheduleDay = parseScheduleDay(schedule.date) ?: return null
        val today = startOfDay(nowMillis)
        if (scheduleDay < today) return null

        if (schedule.subject != "Tugas Sekolah") {
            val parts = schedule.startTime.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: return null
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: return null
            val alarmAt = Calendar.getInstance().apply {
                timeInMillis = scheduleDay
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            return alarmAt.takeIf { it > nowMillis }
        }

        val createdDay = startOfDay(schedule.createdAtMillis)
        val day = Calendar.getInstance().apply {
            timeInMillis = maxOf(createdDay, today)
        }
        val lastDay = Calendar.getInstance().apply { timeInMillis = scheduleDay }
        val reminderHours = listOf(8, 13, 19)
        while (day.timeInMillis <= lastDay.timeInMillis) {
            for (hour in reminderHours) {
                val candidate = (day.clone() as Calendar).apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                if (candidate > nowMillis) return candidate
            }
            day.add(Calendar.DAY_OF_YEAR, 1)
        }
        return null
    }

    fun isValidReminder(schedule: Schedule, expectedAt: Long, nowMillis: Long): Boolean {
        if (schedule.isCompleted || expectedAt > nowMillis) return false
        if (schedule.subject != "Tugas Sekolah") {
            val expected = nextReminderTime(schedule, expectedAt - 1L)
            return expected == expectedAt && scheduleDayIsTodayOrFuture(schedule.date, nowMillis)
        }
        val day = Calendar.getInstance().apply { timeInMillis = expectedAt }
        if (day.get(Calendar.HOUR_OF_DAY) !in listOf(8, 13, 19) ||
            day.get(Calendar.MINUTE) != 0 ||
            day.get(Calendar.SECOND) != 0
        ) return false
        val scheduleDay = parseScheduleDay(schedule.date) ?: return false
        val createdDay = startOfDay(schedule.createdAtMillis)
        val reminderDay = startOfDay(expectedAt)
        return reminderDay >= createdDay &&
            reminderDay <= scheduleDay &&
            startOfDay(nowMillis) <= scheduleDay
    }

    fun reminderPendingIntent(context: Context, scheduleId: Int, expectedAt: Long): PendingIntent {
        val intent = Intent(context, ScheduleReminderReceiver::class.java)
            .setAction(ACTION_REMIND)
            .putExtra(ScheduleNotificationNavigation.EXTRA_SCHEDULE_ID, scheduleId)
            .putExtra(EXTRA_EXPECTED_AT, expectedAt)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_OFFSET_REMINDER + scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun completionPendingIntent(context: Context, scheduleId: Int): PendingIntent {
        val intent = Intent(context, ScheduleReminderReceiver::class.java)
            .setAction(ACTION_COMPLETE)
            .putExtra(ScheduleNotificationNavigation.EXTRA_SCHEDULE_ID, scheduleId)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_OFFSET_COMPLETE + scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun openPendingIntent(context: Context, scheduleId: Int): PendingIntent {
        val intent = Intent(context, com.example.studymate.MainActivity::class.java)
            .setAction(ACTION_OPEN)
            .putExtra(ScheduleNotificationNavigation.EXTRA_SCHEDULE_ID, scheduleId)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(
            context,
            REQUEST_OFFSET_OPEN + scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun parseScheduleDay(date: String): Long? = scheduleDateMillis(date)

    private fun startOfDay(timestamp: Long): Long = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun scheduleDayIsTodayOrFuture(date: String, nowMillis: Long): Boolean {
        val scheduleDay = parseScheduleDay(date) ?: return false
        return scheduleDay >= startOfDay(nowMillis)
    }
}
