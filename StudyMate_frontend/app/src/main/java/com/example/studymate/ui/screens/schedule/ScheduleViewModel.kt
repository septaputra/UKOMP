package com.example.studymate.ui.screens.schedule

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val preferences = appContext.getSharedPreferences("schedules", Context.MODE_PRIVATE)

    private val seed = listOf(
        Schedule(1, "Belajar Matematika", "Belajar", "28 Jan 2026", "19:00", "20:30", "Belajar integral", "Pak Budi"),
        Schedule(2, "Latihan Soal", "Belajar", "28 Jan 2026", "18:00", "19:00", "Kerjakan halaman 45", "Bu Sari"),
        Schedule(3, "Belajar Kimia", "Belajar", "29 Jan 2026", "20:00", "21:00", "", "Pak Agus"),
        Schedule(4, "Sejarah Indonesia", "Sekolah", "30 Jan 2026", "10:00", "11:00", "Bab 4", "Pak Joko"),
        Schedule(5, "Bahasa Inggris", "Sekolah", "31 Jan 2026", "09:00", "10:00", "Latihan speaking", "Bu Tina"),
        Schedule(6, "Belajar Matematika", "Belajar", dateFromToday(0), "19:00", "20:30", "Belajar integral dan latihan soal", "Pak Budi"),
        Schedule(7, "GYM", "Olahraga", dateFromToday(1), "06:00", "07:00", "Latihan pagi", ""),
        Schedule(8, "Menyapu Rumah", "Rumah", dateFromToday(3), "17:00", "17:30", "Bersihkan kamar dan ruang tamu", ""),
        Schedule(9, "Les Bahasa Inggris", "Les", dateFromToday(4), "16:00", "17:30", "Conversation class", "Bu Tina"),
        Schedule(10, "Belajar Sejarah", "Sekolah", dateFromToday(7), "19:00", "20:00", "Bab Pergerakan Nasional", "Pak Joko"),
        Schedule(11, "Jogging", "Olahraga", dateFromToday(-1), "05:30", "06:15", "Jogging pagi", ""),
        Schedule(12, "Belajar Fisika", "Belajar", dateFromToday(-8), "19:00", "20:00", "Review materi gerak", "Bu Sari"),
        Schedule(13, "Rapat Organisasi", "Sekolah", dateFromToday(-35), "15:00", "16:00", "Rapat bulanan", "")
    )

    private val _fullSchedules = MutableStateFlow(ScheduleLocalStore.load(appContext, seed))
    val fullSchedules: StateFlow<List<Schedule>> = _fullSchedules.asStateFlow()

    private val schedulePreferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "items") {
            val saved = ScheduleLocalStore.load(appContext, seed)
            _fullSchedules.value = saved
            ScheduleReminderManager.syncAll(appContext, saved)
        }
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val filteredSchedules: StateFlow<List<Schedule>> = combine(_fullSchedules, _query) { list, q ->
        if (q.isBlank()) list else list.filter { s ->
            s.title.contains(q, ignoreCase = true) ||
                s.subject.contains(q, ignoreCase = true) ||
                s.note.contains(q, ignoreCase = true) ||
                s.teacher.contains(q, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _fullSchedules.value)

    init {
        preferences.registerOnSharedPreferenceChangeListener(schedulePreferenceListener)
        if (!preferences.contains("items")) {
            ScheduleLocalStore.save(appContext, _fullSchedules.value)
        }
        ScheduleReminderManager.syncAll(appContext, _fullSchedules.value)
    }

    fun setQuery(q: String) {
        _query.value = q
    }

    fun addSchedule(title: String, subject: String, date: String, startTime: String, endTime: String, note: String, teacher: String = "") {
        viewModelScope.launch {
            val id = ScheduleLocalStore.reserveNextId(appContext, _fullSchedules.value)
            val s = Schedule(id, title, subject, date, startTime, endTime, note, teacher)
            _fullSchedules.value = _fullSchedules.value + s
            persistAndSchedule()
        }
    }

    fun updateSchedule(id: Int, title: String, subject: String, date: String, startTime: String, endTime: String, note: String, teacher: String = "") {
        viewModelScope.launch {
            _fullSchedules.value = _fullSchedules.value.map {
                if (it.id == id) it.copy(title = title, subject = subject, date = date, startTime = startTime, endTime = endTime, note = note, teacher = teacher) else it
            }
            persistAndSchedule()
        }
    }

    fun deleteSchedule(id: Int) {
        deleteSchedules(setOf(id))
    }

    fun deleteSchedules(ids: Set<Int>) {
        val existingIds = ids intersect _fullSchedules.value.mapTo(mutableSetOf()) { it.id }
        if (existingIds.isEmpty()) return
        viewModelScope.launch {
            existingIds.forEach { id -> ScheduleReminderManager.cancel(appContext, id) }
            _fullSchedules.value = _fullSchedules.value.withoutIds(existingIds)
            persistAndSchedule()
        }
    }

    fun setScheduleCompleted(id: Int, completed: Boolean) {
        _fullSchedules.value = _fullSchedules.value.map { schedule ->
            if (schedule.id == id) schedule.copy(isCompleted = completed) else schedule
        }
        persistAndSchedule()
    }

    fun getById(id: Int): Schedule? = _fullSchedules.value.find { it.id == id }

    private fun persistAndSchedule() {
        ScheduleLocalStore.save(appContext, _fullSchedules.value)
        ScheduleReminderManager.syncAll(appContext, _fullSchedules.value)
    }

    override fun onCleared() {
        preferences.unregisterOnSharedPreferenceChangeListener(schedulePreferenceListener)
        super.onCleared()
    }
}

private fun dateFromToday(dayOffset: Int): String {
    val calendar = java.util.Calendar.getInstance().apply {
        add(java.util.Calendar.DAY_OF_YEAR, dayOffset)
    }
    return java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(calendar.time)
}
