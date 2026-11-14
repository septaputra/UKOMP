package com.example.studymate.ui.screens.schedule

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.studymate.ui.localization.appLanguageForTag
import com.example.studymate.ui.localization.appString

class ScheduleReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getIntExtra(ScheduleNotificationNavigation.EXTRA_SCHEDULE_ID, -1)
        if (scheduleId <= 0) return

        when (intent.action) {
            ScheduleReminderManager.ACTION_COMPLETE -> {
                ScheduleLocalStore.markCompleted(context, scheduleId) ?: return
                ScheduleReminderManager.cancel(context, scheduleId)
                NotificationManagerCompat.from(context).cancel(scheduleId)
                Log.i(TAG, "Schedule $scheduleId marked completed from reminder action")
            }

            ScheduleReminderManager.ACTION_REMIND -> {
                val expectedAt = intent.getLongExtra(ScheduleReminderManager.EXTRA_EXPECTED_AT, -1L)
                if (expectedAt <= 0L) return
                val schedule = ScheduleLocalStore.load(context, emptyList())
                    .firstOrNull { it.id == scheduleId } ?: return
                if (!ScheduleReminderManager.isValidReminder(schedule, expectedAt, System.currentTimeMillis())) {
                    ScheduleReminderManager.schedule(context, schedule)
                    return
                }

                showNotification(context, schedule)
                ScheduleReminderManager.schedule(context, schedule)
            }
        }
    }

    private fun showNotification(context: Context, schedule: Schedule) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return
        val languageTag = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            .getString("language_tag", "id") ?: "id"
        val language = appLanguageForTag(languageTag)
        createNotificationChannel(context, language)
        val timeRange = listOf(schedule.startTime, schedule.endTime)
            .filter { it.isNotBlank() && !(schedule.subject == "Tugas Sekolah" && it == "00:00") }
            .joinToString(" – ")
        val details = listOf(schedule.date, timeRange).filter(String::isNotBlank).joinToString(" · ")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(appString(language, if (schedule.subject == "Tugas Sekolah") "school_task_reminder" else "schedule_reminder"))
            .setContentText(schedule.title)
            .setStyle(NotificationCompat.BigTextStyle().bigText("${schedule.title} · $details"))
            .setContentIntent(ScheduleReminderManager.openPendingIntent(context, schedule.id))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .apply {
                if (schedule.subject == "Tugas Sekolah") {
                    addAction(
                        android.R.drawable.checkbox_on_background,
                        appString(language, "mark_complete"),
                        ScheduleReminderManager.completionPendingIntent(context, schedule.id)
                    )
                    addAction(
                        android.R.drawable.ic_menu_view,
                        appString(language, "open_task"),
                        ScheduleReminderManager.openPendingIntent(context, schedule.id)
                    )
                }
            }
            .build()
        try {
            NotificationManagerCompat.from(context).notify(schedule.id, notification)
        } catch (exception: SecurityException) {
            Log.w(TAG, "Notification permission is unavailable", exception)
        }
    }

    private fun createNotificationChannel(context: Context, language: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                appString(language, "schedule_reminders_channel"),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }

    companion object {
        private const val TAG = "ScheduleReminder"
        private const val CHANNEL_ID = "schedule_reminders"
    }
}

class ScheduleReminderBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED &&
            intent.action != Intent.ACTION_TIMEZONE_CHANGED &&
            intent.action != Intent.ACTION_DATE_CHANGED
        ) return
        val schedules = ScheduleLocalStore.load(context, emptyList())
        ScheduleReminderManager.syncAll(context, schedules)
    }
}
