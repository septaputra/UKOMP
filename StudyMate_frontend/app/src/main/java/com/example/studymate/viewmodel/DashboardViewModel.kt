package com.example.studymate.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

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
    private val repository: DashboardRepository = InMemoryDashboardRepository()
    private val _dashboardData = MutableStateFlow(DashboardData())
    val dashboardData: StateFlow<DashboardData> = _dashboardData.asStateFlow()
    val banners = repository.banners
    val news = repository.news
    val tutoringCenters = repository.tutoringCenters
    val learningVideos = repository.learningVideos
    val notifications = repository.notifications

    fun findNews(id: Int): DashboardNewsItem? = news.find { it.id == id }

    fun findTutoringCenter(id: Int): TutoringCenter? = tutoringCenters.find { it.id == id }

    fun findLearningVideo(id: Int): LearningVideo? = learningVideos.find { it.id == id }

    fun findNotification(id: Int): StudyNotification? = repository.findNotification(id)

    fun filterLearningVideos(query: String, educationLevel: String, subject: String): List<LearningVideo> {
        val term = query.trim()
        return learningVideos.filter { video ->
            val matchesQuery = term.isBlank() || listOf(
                video.title,
                video.subject,
                video.educationLevel,
                video.description
            ).any { it.contains(term, ignoreCase = true) }
            val matchesLevel = educationLevel == "Semua" ||
                video.educationLevel.split('/').any { it.trim().equals(educationLevel, ignoreCase = true) }
            val matchesSubject = subject == "Semua" || when (subject) {
                "Fisika dan Kimia" -> video.subject.equals("Fisika", ignoreCase = true) ||
                    video.subject.equals("Kimia", ignoreCase = true) ||
                    video.subject.equals("Fisika dan Kimia", ignoreCase = true)
                else -> video.subject.equals(subject, ignoreCase = true)
            }
            matchesQuery && matchesLevel && matchesSubject
        }
    }

    fun filterTutoringCenters(
        query: String,
        location: String,
        subject: String,
        province: String = "Semua Provinsi",
        educationLevel: String = "Semua"
    ): List<TutoringCenter> {
        val term = query.trim()
        return tutoringCenters.filter { center ->
            val matchesQuery = term.isBlank() || listOf(
                center.name,
                center.category,
                center.location,
                center.description,
                center.address.orEmpty(),
                center.province.orEmpty(),
                center.city.orEmpty(),
                center.educationLevel.orEmpty(),
                center.contactNumber.orEmpty(),
                center.website.orEmpty()
            ).any { it.contains(term, ignoreCase = true) }
            val matchesLocation = location == "Semua" || (center.city?.takeIf(String::isNotBlank) ?: center.location) == location
            val matchesProvince = province == "Semua Provinsi" || center.province == province
            val matchesEducation = educationLevel == "Semua" ||
                center.educationLevel.orEmpty().split(Regex("[/,–-]"))
                    .any { it.trim().equals(educationLevel, ignoreCase = true) }
            val matchesSubject = subject == "Semua" || center.category.equals(subject, ignoreCase = true)
            matchesQuery && matchesLocation && matchesProvince && matchesEducation && matchesSubject
        }
    }

    fun markNotificationRead(id: Int) = repository.markNotificationRead(id)

    fun markAllNotificationsRead() = repository.markAllNotificationsRead()

    fun filterNews(query: String): List<DashboardNewsItem> {
        val terms = searchTerms(query)
        if (terms.isEmpty()) return news

        return news.filter { item ->
            matchesAllTerms(
                terms,
                item.title,
                item.category,
                item.summary,
                item.content,
                item.sourceName.orEmpty(),
                "berita berita pendidikan"
            )
        }
    }

    fun searchContent(query: String): List<DashboardSearchResult> {
        val terms = searchTerms(query)
        if (terms.isEmpty()) return emptyList()

        return buildList {
            if (matchesAllTerms(terms, "Progress Belajar Fitur Utama StudyMate")) {
                add(DashboardSearchResult(1, "Progress Belajar", "Fitur StudyMate", DashboardResultType.Feature, "progress"))
            }

            banners.filter { banner ->
                matchesAllTerms(
                    terms,
                    banner.title,
                    banner.subtitle,
                    banner.actionText,
                    banner.destination,
                    "banner promosi informasi"
                )
            }.forEach {
                add(DashboardSearchResult(it.id, it.title, it.subtitle, DashboardResultType.Banner, it.destination))
            }

            tutoringCenters.filter { center ->
                matchesAllTerms(
                    terms,
                    center.name,
                    center.category,
                    center.location,
                    center.description,
                    center.sampleFee.orEmpty(),
                    center.address.orEmpty(),
                    center.province.orEmpty(),
                    center.city.orEmpty(),
                    center.educationLevel.orEmpty(),
                    center.contactNumber.orEmpty(),
                    center.website.orEmpty(),
                    "bimbel info bimbel tempat lembaga layanan"
                )
            }.forEach {
                add(DashboardSearchResult(it.id, it.name, "${it.category} · ${it.location}", DashboardResultType.Tutoring))
            }

            learningVideos.filter { video ->
                matchesAllTerms(
                    terms,
                    video.title,
                    video.subject,
                    video.educationLevel,
                    video.description,
                    video.sourceName,
                    video.duration,
                    "materi materi video video pembelajaran pelajaran pendidikan"
                )
            }.forEach {
                add(DashboardSearchResult(it.id, it.title, "${it.subject} · ${it.educationLevel}", DashboardResultType.Video))
            }

            news.filter { item ->
                matchesAllTerms(
                    terms,
                    item.title,
                    item.category,
                    item.summary,
                    item.content,
                    item.sourceName.orEmpty(),
                    "berita berita pendidikan"
                )
            }.forEach {
                add(DashboardSearchResult(it.id, it.title, it.category, DashboardResultType.News))
            }
        }
    }

    private fun searchTerms(query: String): List<String> =
        query.trim().lowercase(Locale.ROOT).split(Regex("\\s+")).filter(String::isNotBlank)

    private fun matchesAllTerms(terms: List<String>, vararg fields: String): Boolean {
        val searchableText = fields.joinToString(" ").lowercase(Locale.ROOT)
        return terms.all(searchableText::contains)
    }

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