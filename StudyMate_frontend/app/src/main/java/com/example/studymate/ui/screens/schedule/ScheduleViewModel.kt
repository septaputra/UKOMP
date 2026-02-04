package com.example.studymate.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {
    private var nextId = 4

    private val seed = listOf(
        Schedule(1, "Belajar Matematika", "Matematika", "28 Jan 2026", "19:00", "20:30", "Belajar integral", "Pak Budi"),
        Schedule(2, "Latihan Soal", "Fisika", "28 Jan 2026", "18:00", "19:00", "Kerjakan halaman 45", "Bu Sari"),
        Schedule(3, "Belajar Kimia", "Kimia", "29 Jan 2026", "20:00", "21:00", "", "Pak Agus"),
        Schedule(4, "Sejarah Indonesia", "Sejarah", "30 Jan 2026", "10:00", "11:00", "Bab 4", "Pak Joko"),
        Schedule(5, "Bahasa Inggris", "Bahasa Inggris", "31 Jan 2026", "09:00", "10:00", "Latihan speaking", "Bu Tina")
    )

    private val _fullSchedules = MutableStateFlow(seed)
    val fullSchedules: StateFlow<List<Schedule>> = _fullSchedules.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val filteredSchedules: StateFlow<List<Schedule>> = combine(_fullSchedules, _query) { list, q ->
        if (q.isBlank()) list else list.filter { s ->
            s.title.contains(q, ignoreCase = true) || s.subject.contains(q, ignoreCase = true) || s.teacher.contains(q, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _fullSchedules.value)

    fun setQuery(q: String) {
        _query.value = q
    }

    fun addSchedule(title: String, subject: String, date: String, startTime: String, endTime: String, note: String, teacher: String = "") {
        viewModelScope.launch {
            val s = Schedule(nextId++, title, subject, date, startTime, endTime, note, teacher)
            _fullSchedules.value = _fullSchedules.value + s
        }
    }

    fun updateSchedule(id: Int, title: String, subject: String, date: String, startTime: String, endTime: String, note: String, teacher: String = "") {
        viewModelScope.launch {
            _fullSchedules.value = _fullSchedules.value.map {
                if (it.id == id) it.copy(title = title, subject = subject, date = date, startTime = startTime, endTime = endTime, note = note, teacher = teacher) else it
            }
        }
    }

    fun deleteSchedule(id: Int) {
        viewModelScope.launch {
            _fullSchedules.value = _fullSchedules.value.filterNot { it.id == id }
        }
    }

    fun getById(id: Int): Schedule? = _fullSchedules.value.find { it.id == id }
}
