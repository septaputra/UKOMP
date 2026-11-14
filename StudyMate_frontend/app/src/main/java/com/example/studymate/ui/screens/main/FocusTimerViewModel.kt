package com.example.studymate.ui.screens.main

import android.os.SystemClock
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

internal enum class FocusTimerMode {
    Focus,
    ShortBreak,
    LongBreak,
    Custom
}

internal data class FocusTimerState(
    val focusDurationMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val customHours: Int = 0,
    val customMinutes: Int = 0,
    val customSeconds: Int = 0,
    val mode: FocusTimerMode = FocusTimerMode.Focus,
    val totalDurationMillis: Long = 25 * 60_000L,
    val remainingMillis: Long = 25 * 60_000L,
    val isRunning: Boolean = false,
    val focusSessionCount: Int = 0,
    val completedSessionsToday: Int = 0,
    val totalFocusMinutesToday: Int = 0,
    val completionCount: Int = 0,
    val completedFocusSessions: List<FocusSession> = emptyList()
)

internal data class FocusSession(
    val id: String,
    val completedAtMillis: Long,
    val durationMillis: Long
)

internal class FocusTimerViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences("focus_history", Context.MODE_PRIVATE)
    private val loadedSessions = loadCompletedSessions()
    private val _state = MutableStateFlow(
        FocusTimerState(
            completedFocusSessions = loadedSessions,
            completedSessionsToday = loadedSessions.count { isToday(it.completedAtMillis) },
            totalFocusMinutesToday = (loadedSessions.filter { isToday(it.completedAtMillis) }
                .sumOf { it.durationMillis } / 60_000L).toInt()
        )
    )
    val state: StateFlow<FocusTimerState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var deadlineMillis = 0L

    fun setFocusDuration(minutes: Int) {
        _state.value = _state.value.copy(focusDurationMinutes = minutes).updateIdleDuration(FocusTimerMode.Focus)
    }

    fun setShortBreakDuration(minutes: Int) {
        _state.value = _state.value.copy(shortBreakMinutes = minutes).updateIdleDuration(FocusTimerMode.ShortBreak)
    }

    fun setLongBreakDuration(minutes: Int) {
        _state.value = _state.value.copy(longBreakMinutes = minutes).updateIdleDuration(FocusTimerMode.LongBreak)
    }

    fun setCustomHours(value: Int) {
        updateCustomDuration { it.copy(customHours = value.coerceIn(0, 99)) }
    }

    fun setCustomMinutes(value: Int) {
        updateCustomDuration { it.copy(customMinutes = value.coerceIn(0, 59)) }
    }

    fun setCustomSeconds(value: Int) {
        updateCustomDuration { it.copy(customSeconds = value.coerceIn(0, 59)) }
    }

    fun setCustomPreset(totalSeconds: Int) {
        val safeSeconds = totalSeconds.coerceAtLeast(1)
        updateCustomDuration {
            it.copy(
                customHours = safeSeconds / 3600,
                customMinutes = (safeSeconds % 3600) / 60,
                customSeconds = safeSeconds % 60
            )
        }
    }

    fun usePomodoroMode(): Boolean {
        if (_state.value.isRunning) return false
        timerJob?.cancel()
        val current = _state.value
        val duration = durationFor(current, FocusTimerMode.Focus)
        _state.value = current.copy(
            mode = FocusTimerMode.Focus,
            totalDurationMillis = duration,
            remainingMillis = duration
        )
        return true
    }

    fun startPomodoro() {
        val current = _state.value
        if (current.isRunning || current.mode == FocusTimerMode.Custom) return
        val duration = durationFor(current, current.mode)
        val remaining = current.remainingMillis.takeIf { it > 0L } ?: duration
        beginCountdown(current.copy(totalDurationMillis = duration, remainingMillis = remaining))
    }

    fun startCustomTimer(): Boolean {
        val current = _state.value
        if (current.isRunning) return false
        val duration = customDurationMillis(current)
        if (duration <= 0L) return false
        timerJob?.cancel()
        val isPausedCustomSession =
            current.mode == FocusTimerMode.Custom &&
                current.remainingMillis in 1 until current.totalDurationMillis
        beginCountdown(
            current.copy(
                mode = FocusTimerMode.Custom,
                totalDurationMillis = duration,
                remainingMillis = if (isPausedCustomSession) current.remainingMillis else duration
            )
        )
        return true
    }

    fun pause() {
        val current = _state.value
        if (!current.isRunning) return
        val remaining = (deadlineMillis - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
        timerJob?.cancel()
        timerJob = null
        if (remaining == 0L) {
            _state.value = current.copy(remainingMillis = 0L)
            completeCurrentTimer()
        } else {
            _state.value = current.copy(remainingMillis = remaining, isRunning = false)
        }
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
        val current = _state.value
        if (current.mode == FocusTimerMode.Custom) {
            val duration = customDurationMillis(current)
            _state.value = current.copy(
                totalDurationMillis = duration,
                remainingMillis = duration,
                isRunning = false
            )
        } else {
            val duration = durationFor(current, FocusTimerMode.Focus)
            _state.value = current.copy(
                mode = FocusTimerMode.Focus,
                totalDurationMillis = duration,
                remainingMillis = duration,
                isRunning = false,
                focusSessionCount = 0
            )
        }
    }

    private fun beginCountdown(state: FocusTimerState) {
        timerJob?.cancel()
        val now = SystemClock.elapsedRealtime()
        deadlineMillis = now + state.remainingMillis
        _state.value = state.copy(isRunning = true)
        timerJob = viewModelScope.launch {
            while (true) {
                val remaining = (deadlineMillis - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
                _state.value = _state.value.copy(remainingMillis = remaining)
                if (remaining == 0L) {
                    completeCurrentTimer()
                    break
                }
                delay(50L)
            }
            timerJob = null
        }
    }

    private fun completeCurrentTimer() {
        timerJob?.cancel()
        timerJob = null
        val current = _state.value
        if (!current.isRunning) return
        when (current.mode) {
            FocusTimerMode.Custom -> {
                _state.value = current.copy(
                    remainingMillis = 0L,
                    isRunning = false,
                    completionCount = current.completionCount + 1
                )
            }
            FocusTimerMode.Focus -> {
                val completedAt = System.currentTimeMillis()
                val session = FocusSession(
                    id = UUID.randomUUID().toString(),
                    completedAtMillis = completedAt,
                    durationMillis = current.totalDurationMillis
                )
                val sessions = current.completedFocusSessions + session
                preferences.edit().putString("completed_sessions", JSONArray().apply {
                    sessions.forEach { item ->
                        put(JSONObject().apply {
                            put("id", item.id)
                            put("completedAt", item.completedAtMillis)
                            put("duration", item.durationMillis)
                        })
                    }
                }.toString()).apply()
                val completedFocusCount = current.focusSessionCount + 1
                val nextMode = if (completedFocusCount % 4 == 0) {
                    FocusTimerMode.LongBreak
                } else {
                    FocusTimerMode.ShortBreak
                }
                val nextDuration = durationFor(current, nextMode)
                _state.value = current.copy(
                    mode = nextMode,
                    totalDurationMillis = nextDuration,
                    remainingMillis = nextDuration,
                    isRunning = false,
                    focusSessionCount = completedFocusCount,
                    completedSessionsToday = sessions.count { isToday(it.completedAtMillis) },
                    totalFocusMinutesToday = (sessions.filter { isToday(it.completedAtMillis) }
                        .sumOf { it.durationMillis } / 60_000L).toInt(),
                    completedFocusSessions = sessions,
                    completionCount = current.completionCount + 1
                )
            }

            FocusTimerMode.ShortBreak, FocusTimerMode.LongBreak -> {
                val nextDuration = durationFor(current, FocusTimerMode.Focus)
                _state.value = current.copy(
                    mode = FocusTimerMode.Focus,
                    totalDurationMillis = nextDuration,
                    remainingMillis = nextDuration,
                    isRunning = false,
                    completionCount = current.completionCount + 1
                )
            }
        }
    }

    private fun loadCompletedSessions(): List<FocusSession> {
        val json = preferences.getString("completed_sessions", null) ?: return emptyList()
        val array = runCatching { JSONArray(json) }.getOrElse { return emptyList() }
        return buildList {
            for (index in 0 until array.length()) {
                val item = runCatching { array.getJSONObject(index) }.getOrNull() ?: continue
                val id = item.optString("id").takeIf { it.isNotBlank() } ?: continue
                val completedAt = item.optLong("completedAt", 0L).takeIf { it > 0L } ?: continue
                val duration = item.optLong("duration", 0L).takeIf { it > 0L } ?: continue
                add(FocusSession(id, completedAt, duration))
            }
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        val sessionCalendar = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        val today = java.util.Calendar.getInstance()
        return sessionCalendar.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR) &&
            sessionCalendar.get(java.util.Calendar.DAY_OF_YEAR) == today.get(java.util.Calendar.DAY_OF_YEAR)
    }

    private fun updateCustomDuration(transform: (FocusTimerState) -> FocusTimerState) {
        val current = _state.value
        val updated = transform(current)
        if (updated.isRunning) return
        val duration = customDurationMillis(updated)
        val hasPausedCustomSession =
            updated.mode == FocusTimerMode.Custom &&
                current.remainingMillis in 1 until current.totalDurationMillis
        _state.value = updated.copy(
            totalDurationMillis = if (updated.mode == FocusTimerMode.Custom) duration else updated.totalDurationMillis,
            remainingMillis = if (updated.mode == FocusTimerMode.Custom && !hasPausedCustomSession) {
                duration
            } else {
                current.remainingMillis
            }
        )
    }

    private fun FocusTimerState.updateIdleDuration(mode: FocusTimerMode): FocusTimerState {
        if (isRunning || this.mode != mode) return this
        val duration = durationFor(this, mode)
        return copy(totalDurationMillis = duration, remainingMillis = duration)
    }

    private fun durationFor(state: FocusTimerState, mode: FocusTimerMode): Long = when (mode) {
        FocusTimerMode.Focus -> state.focusDurationMinutes * 60_000L
        FocusTimerMode.ShortBreak -> state.shortBreakMinutes * 60_000L
        FocusTimerMode.LongBreak -> state.longBreakMinutes * 60_000L
        FocusTimerMode.Custom -> customDurationMillis(state)
    }

    private fun customDurationMillis(state: FocusTimerState): Long =
        timerTotalSeconds(state.customHours, state.customMinutes, state.customSeconds) * 1000L
}
