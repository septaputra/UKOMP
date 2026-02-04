package com.example.studymate.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardData(
    val scheduleCount: Int = 3,
    val unfinishedTasksCount: Int = 5,
    val notesCount: Int = 12,
    val completedTasks: Int = 4,
    val totalTasks: Int = 10
) {
    val progressPercentage: Float
        get() = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0f
}

class DashboardViewModel : ViewModel() {
    private val _dashboardData = MutableStateFlow(DashboardData())
    val dashboardData: StateFlow<DashboardData> = _dashboardData.asStateFlow()

    // Methods to update data (would be connected to actual data source in real app)
    fun updateScheduleCount(count: Int) {
        _dashboardData.value = _dashboardData.value.copy(scheduleCount = count)
    }

    fun updateUnfinishedTasksCount(count: Int) {
        _dashboardData.value = _dashboardData.value.copy(unfinishedTasksCount = count)
    }

    fun updateNotesCount(count: Int) {
        _dashboardData.value = _dashboardData.value.copy(notesCount = count)
    }

    fun updateCompletedTasks(count: Int) {
        _dashboardData.value = _dashboardData.value.copy(completedTasks = count)
    }

    fun updateTotalTasks(count: Int) {
        _dashboardData.value = _dashboardData.value.copy(totalTasks = count)
    }
}