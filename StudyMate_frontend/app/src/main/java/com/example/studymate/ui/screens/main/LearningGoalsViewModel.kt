package com.example.studymate.ui.screens.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.studymate.ui.screens.notes.Note
import com.example.studymate.ui.screens.schedule.Schedule
import com.example.studymate.ui.screens.schedule.ScheduleGroup
import com.example.studymate.ui.screens.schedule.groupFor
import com.example.studymate.ui.screens.schedule.startOfTodayMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import kotlin.math.roundToInt

internal enum class LearningGoalType(val label: String, val unit: String) {
    LEARNING_ACTIVITIES("Aktivitas belajar selesai", "aktivitas"),
    SCHOOL_TASKS("Tugas sekolah selesai", "tugas"),
    NOTES("Membuat catatan", "catatan"),
    FOCUS_SESSIONS("Sesi Fokus Belajar selesai", "sesi"),
    FOCUS_MINUTES("Durasi Fokus Belajar", "menit")
}

internal data class LearningGoal(
    val id: String,
    val name: String,
    val type: LearningGoalType,
    val targetValue: Int,
    val deadlineMillis: Long?,
    val description: String,
    val completedAtMillis: Long? = null
)

internal data class LearningAward(
    val id: String,
    val goalId: String,
    val goalName: String,
    val goalType: LearningGoalType,
    val earnedAtMillis: Long
)

internal data class LearningProgressMetrics(
    val totalActivities: Int,
    val eligibleSchedules: Int,
    val completedActivities: Int,
    val remainingActivities: Int,
    val schoolTaskCount: Int,
    val completedSchoolTasks: Int,
    val remainingSchoolTasks: Int,
    val noteCount: Int,
    val focusSessions: Int,
    val focusDurationMinutes: Int
) {
    val schoolTaskCompletionPercent: Int
        get() = if (schoolTaskCount == 0) 0 else
            (completedSchoolTasks * 100f / schoolTaskCount).roundToInt()

    fun valueFor(type: LearningGoalType): Int = when (type) {
        LearningGoalType.LEARNING_ACTIVITIES -> completedActivities
        LearningGoalType.SCHOOL_TASKS -> completedSchoolTasks
        LearningGoalType.NOTES -> noteCount
        LearningGoalType.FOCUS_SESSIONS -> focusSessions
        LearningGoalType.FOCUS_MINUTES -> focusDurationMinutes
    }

    companion object {
        fun from(
            schedules: List<Schedule>,
            notes: List<Note>,
            sessions: List<FocusSession>,
            todayStartMillis: Long = startOfTodayMillis()
        ): LearningProgressMetrics {
            val uniqueSchedules = schedules.distinctBy { it.id }
            val scheduled = uniqueSchedules.filter {
                it.groupFor(todayStartMillis) in setOf(ScheduleGroup.TODAY, ScheduleGroup.UPCOMING)
            }
            val previousIncomplete = uniqueSchedules.count {
                it.groupFor(todayStartMillis) == ScheduleGroup.PREVIOUS && !it.isCompleted
            }
            val schoolTasks = uniqueSchedules.filter { it.subject == "Tugas Sekolah" }
            val uniqueSessions = sessions.distinctBy { it.id }
            return LearningProgressMetrics(
                totalActivities = uniqueSchedules.size,
                eligibleSchedules = scheduled.size,
                completedActivities = uniqueSchedules.count { it.isCompleted },
                remainingActivities = previousIncomplete,
                schoolTaskCount = schoolTasks.size,
                completedSchoolTasks = schoolTasks.count { it.isCompleted },
                remainingSchoolTasks = schoolTasks.count { !it.isCompleted },
                noteCount = notes.distinctBy { it.id }.size,
                focusSessions = uniqueSessions.size,
                focusDurationMinutes = (uniqueSessions.sumOf { it.durationMillis } / 60_000L).toInt()
            )
        }
    }
}

internal class LearningGoalsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences("learning_goals", Context.MODE_PRIVATE)
    private val _goals = MutableStateFlow(loadGoals())
    val goals: StateFlow<List<LearningGoal>> = _goals.asStateFlow()
    private val _awards = MutableStateFlow(loadAwards())
    val awards: StateFlow<List<LearningAward>> = _awards.asStateFlow()

    fun addGoal(
        name: String,
        type: LearningGoalType,
        targetValue: Int,
        deadlineMillis: Long?,
        description: String
    ) {
        val goal = LearningGoal(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            type = type,
            targetValue = targetValue,
            deadlineMillis = deadlineMillis,
            description = description.trim()
        )
        _goals.value = _goals.value + goal
        saveGoals()
    }

    fun getGoal(id: String): LearningGoal? = _goals.value.firstOrNull { it.id == id }

    fun updateGoal(
        id: String,
        name: String,
        type: LearningGoalType,
        targetValue: Int,
        deadlineMillis: Long?,
        description: String
    ) {
        _goals.value = _goals.value.map { goal ->
            if (goal.id == id) {
                goal.copy(
                    name = name.trim(),
                    type = type,
                    targetValue = targetValue,
                    deadlineMillis = deadlineMillis,
                    description = description.trim()
                )
            } else {
                goal
            }
        }
        saveGoals()
    }

    fun deleteGoal(id: String) {
        _goals.value = _goals.value.filterNot { it.id == id }
        saveGoals()
    }

    fun refreshAchievements(metrics: LearningProgressMetrics) {
        val newlyCompleted = _goals.value.filter {
            it.completedAtMillis == null && metrics.valueFor(it.type) >= it.targetValue
        }
        if (newlyCompleted.isEmpty()) return

        val completedAt = System.currentTimeMillis()
        val completedIds = newlyCompleted.mapTo(mutableSetOf()) { it.id }
        _goals.value = _goals.value.map {
            if (it.id in completedIds) it.copy(completedAtMillis = completedAt) else it
        }
        val existingAwardGoalIds = _awards.value.mapTo(mutableSetOf()) { it.goalId }
        _awards.value = _awards.value + newlyCompleted
            .filterNot { it.id in existingAwardGoalIds }
            .map { goal ->
                LearningAward(
                    id = UUID.randomUUID().toString(),
                    goalId = goal.id,
                    goalName = goal.name,
                    goalType = goal.type,
                    earnedAtMillis = completedAt
                )
            }
        saveGoals()
        saveAwards()
    }

    private fun loadGoals(): List<LearningGoal> = readArray("goals").mapNotNull { item ->
        runCatching {
            LearningGoal(
                id = item.getString("id"),
                name = item.getString("name"),
                type = LearningGoalType.valueOf(item.getString("type")),
                targetValue = item.getInt("target"),
                deadlineMillis = item.takeIf { it.has("deadline") && !it.isNull("deadline") }?.getLong("deadline"),
                description = item.optString("description"),
                completedAtMillis = item.takeIf { it.has("completedAt") && !it.isNull("completedAt") }?.getLong("completedAt")
            )
        }.getOrNull()
    }

    private fun loadAwards(): List<LearningAward> = readArray("awards").mapNotNull { item ->
        runCatching {
            LearningAward(
                id = item.getString("id"),
                goalId = item.getString("goalId"),
                goalName = item.getString("goalName"),
                goalType = LearningGoalType.valueOf(item.getString("type")),
                earnedAtMillis = item.getLong("earnedAt")
            )
        }.getOrNull()
    }

    private fun readArray(key: String): List<JSONObject> {
        val json = preferences.getString(key, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(json)
            List(array.length()) { array.getJSONObject(it) }
        }.getOrElse { emptyList() }
    }

    private fun saveGoals() {
        preferences.edit().putString("goals", JSONArray().apply {
            _goals.value.forEach { goal ->
                put(JSONObject().apply {
                    put("id", goal.id)
                    put("name", goal.name)
                    put("type", goal.type.name)
                    put("target", goal.targetValue)
                    if (goal.deadlineMillis == null) put("deadline", JSONObject.NULL) else put("deadline", goal.deadlineMillis)
                    put("description", goal.description)
                    if (goal.completedAtMillis == null) put("completedAt", JSONObject.NULL) else put("completedAt", goal.completedAtMillis)
                })
            }
        }.toString()).apply()
    }

    private fun saveAwards() {
        preferences.edit().putString("awards", JSONArray().apply {
            _awards.value.forEach { award ->
                put(JSONObject().apply {
                    put("id", award.id)
                    put("goalId", award.goalId)
                    put("goalName", award.goalName)
                    put("type", award.goalType.name)
                    put("earnedAt", award.earnedAtMillis)
                })
            }
        }.toString()).apply()
    }
}
