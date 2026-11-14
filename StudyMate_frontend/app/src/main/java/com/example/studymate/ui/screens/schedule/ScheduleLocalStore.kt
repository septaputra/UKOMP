package com.example.studymate.ui.screens.schedule

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

internal object ScheduleLocalStore {
    private const val PREFERENCES = "schedules"
    private const val ITEMS_KEY = "items"

    fun load(context: Context, fallback: List<Schedule>): List<Schedule> {
        val json = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getString(ITEMS_KEY, null) ?: return fallback
        return try {
            val array = JSONArray(json)
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                Schedule(
                    id = item.getInt("id"),
                    title = item.getString("title"),
                    subject = item.getString("subject"),
                    date = item.getString("date"),
                    startTime = item.getString("startTime"),
                    endTime = item.getString("endTime"),
                    note = item.optString("note"),
                    teacher = item.optString("teacher"),
                    isCompleted = item.optBoolean("isCompleted"),
                    createdAtMillis = item.optLong("createdAtMillis", System.currentTimeMillis())
                )
            }
        } catch (exception: Exception) {
            Log.e("ScheduleLocalStore", "Unable to load saved schedules", exception)
            fallback
        }
    }

    fun save(context: Context, schedules: List<Schedule>) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit()
            .putString(ITEMS_KEY, JSONArray().apply {
                schedules.forEach { schedule ->
                    put(JSONObject().apply {
                        put("id", schedule.id)
                        put("title", schedule.title)
                        put("subject", schedule.subject)
                        put("date", schedule.date)
                        put("startTime", schedule.startTime)
                        put("endTime", schedule.endTime)
                        put("note", schedule.note)
                        put("teacher", schedule.teacher)
                        put("isCompleted", schedule.isCompleted)
                        put("createdAtMillis", schedule.createdAtMillis)
                    })
                }
            }.toString())
            .apply()
    }

    fun reserveNextId(context: Context, schedules: List<Schedule>): Int {
        val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        val id = maxOf(preferences.getInt("next_id", 1), (schedules.maxOfOrNull { it.id } ?: 0) + 1)
        preferences.edit().putInt("next_id", id + 1).apply()
        return id
    }

    fun markCompleted(context: Context, scheduleId: Int): Schedule? {
        val schedules = load(context, emptyList())
        val updated = schedules.map { schedule ->
            if (schedule.id == scheduleId) schedule.copy(isCompleted = true) else schedule
        }
        if (updated.none { it.id == scheduleId }) return null
        save(context, updated)
        return updated.first { it.id == scheduleId }
    }
}
