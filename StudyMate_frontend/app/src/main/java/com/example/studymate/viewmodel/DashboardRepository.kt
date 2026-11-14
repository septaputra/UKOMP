package com.example.studymate.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class BannerArtwork { Exam, Tips, Tutoring }

data class DashboardBanner(
    val id: Int,
    val title: String,
    val subtitle: String,
    val actionText: String,
    val destination: String,
    val artwork: BannerArtwork
)

data class DashboardNewsItem(
    val id: Int,
    val title: String,
    val summary: String,
    val content: String,
    val publishedAt: String,
    val category: String,
    val sourceName: String? = null,
    val sourceUrl: String? = null,
    val thumbnailUrl: String? = null
)

data class TutoringCenter(
    val id: Int,
    val name: String,
    val category: String,
    val location: String,
    val description: String,
    val sampleFee: String? = null,
    val address: String? = null,
    val province: String? = null,
    val city: String? = null,
    val educationLevel: String? = null,
    val contactNumber: String? = null,
    val website: String? = null,
    val imageUrl: String? = null
)

data class LearningVideo(
    val id: Int,
    val title: String,
    val subject: String,
    val educationLevel: String,
    val sourceName: String,
    val duration: String,
    val description: String,
    val videoUrl: String? = null
)

data class StudyNotification(
    val id: Int,
    val title: String,
    val message: String,
    val timeLabel: String,
    val isRead: Boolean
)

enum class DashboardResultType { Tutoring, Video, News, Banner, Feature }

data class DashboardSearchResult(
    val id: Int,
    val title: String,
    val supportingText: String,
    val type: DashboardResultType,
    val destination: String? = null
)

interface DashboardRepository {
    val banners: List<DashboardBanner>
    val news: List<DashboardNewsItem>
    val tutoringCenters: List<TutoringCenter>
    val learningVideos: List<LearningVideo>
    val notifications: StateFlow<List<StudyNotification>>

    fun findNotification(id: Int): StudyNotification?
    fun markNotificationRead(id: Int)
    fun markAllNotificationsRead()
}

class InMemoryDashboardRepository : DashboardRepository {
    override val banners = listOf(
        DashboardBanner(1, "Siap Hadapi UTBK 2027?", "Belajar lebih terarah bersama StudyMate!", "Mulai Belajar", "learning_videos", BannerArtwork.Exam),
        DashboardBanner(2, "Tips Belajar Efektif", "Temukan strategi belajar yang cocok untukmu", "Baca Tips", "news_detail/1", BannerArtwork.Tips),
        DashboardBanner(3, "Temukan Bimbel Terbaik", "Kenali pilihan belajar sesuai kebutuhanmu", "Lihat Bimbel", "tutoring_list", BannerArtwork.Tutoring)
    )

    override val news = listOf(
        DashboardNewsItem(
            1,
            "Tips Belajar Efektif untuk Menghadapi Ujian",
            "Menyusun target kecil dan jadwal belajar dapat membantu menjaga konsistensi.",
            "Mulailah dengan membagi materi menjadi bagian yang lebih kecil. Sisihkan waktu istirahat, ulangi materi secara berkala, dan evaluasi bagian yang masih perlu dipelajari. Sesuaikan metode belajar dengan kebutuhanmu.",
            "12 Mei 2026",
            "Tips Belajar",
            thumbnailUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&w=800&q=80"
        ),
        DashboardNewsItem(
            2,
            "Informasi Seleksi Masuk Perguruan Tinggi",
            "Kenali tahapan seleksi dan siapkan dokumen sejak awal.",
            "Setiap jalur seleksi memiliki jadwal dan persyaratan yang berbeda. Catat tenggat waktu, periksa informasi resmi dari institusi tujuan, dan siapkan dokumen yang diperlukan lebih awal.",
            "10 Mei 2026",
            "Perguruan Tinggi",
            thumbnailUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?auto=format&fit=crop&w=800&q=80"
        ),
        DashboardNewsItem(
            3,
            "Persiapan Siswa Menghadapi Tahun Ajaran Baru",
            "Rutinitas sederhana dapat membantu memulai kegiatan belajar dengan lebih teratur.",
            "Sebelum tahun ajaran dimulai, tinjau kembali materi dasar, rapikan perlengkapan, dan buat rencana belajar yang realistis. Beri ruang untuk menyesuaikan rutinitas baru secara bertahap.",
            "8 Mei 2026",
            "Pendidikan",
            thumbnailUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=800&q=80"
        ),
        DashboardNewsItem(
            4,
            "Strategi Belajar untuk Meningkatkan Prestasi",
            "Konsistensi dan evaluasi rutin membantu mengenali kemajuan belajar.",
            "Gunakan sesi belajar terfokus, catat hal yang belum dipahami, lalu evaluasi kemajuan setiap pekan. Jika menemui kesulitan, mintalah bantuan guru atau teman belajar.",
            "5 Mei 2026",
            "SMA",
            thumbnailUrl = "https://images.unsplash.com/photo-1455390582262-044cdead277a?auto=format&fit=crop&w=800&q=80"
        ),
        DashboardNewsItem(
            5,
            "Membangun Kebiasaan Membaca Setiap Hari",
            "Mulai dengan durasi singkat dan pilih bacaan yang menarik.",
            "Kebiasaan membaca dapat dimulai dari beberapa menit setiap hari. Pilih bahan bacaan yang sesuai minat, simpan catatan singkat, dan tingkatkan durasi secara perlahan.",
            "2 Mei 2026",
            "Literasi",
            thumbnailUrl = "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?auto=format&fit=crop&w=800&q=80"
        )
    )

    override val tutoringCenters = listOf(
        TutoringCenter(1, "Ruang Belajar Cerdas", "Matematika", "Jakarta Selatan (contoh)", "Sesi pendampingan matematika kelompok kecil untuk tingkat SMA.", "Contoh: Rp150.000/sesi"),
        TutoringCenter(2, "Kelas Sains Mandiri", "Fisika dan Kimia", "Bandung (contoh)", "Latihan konsep sains dan pembahasan soal secara terarah.", "Contoh: Rp125.000/sesi"),
        TutoringCenter(3, "English Study Circle", "Bahasa Inggris", "Yogyakarta (contoh)", "Latihan percakapan dan pemahaman bacaan dalam kelompok belajar.", null),
        TutoringCenter(4, "Teman UTBK", "Persiapan UTBK", "Daring (contoh)", "Kelompok belajar persiapan ujian dengan target mingguan.", null)
    )

    override val learningVideos = listOf(
        LearningVideo(1, "Memahami Integral Dasar", "Matematika", "SMA", "Materi StudyMate", "12 menit", "Pengenalan integral dan contoh soal dasar."),
        LearningVideo(2, "Hukum Gerak Newton", "Fisika", "SMA", "Materi StudyMate", "10 menit", "Ringkasan tiga hukum gerak Newton."),
        LearningVideo(3, "Fotosintesis dan Respirasi", "Biologi", "SMA", "Materi StudyMate", "14 menit", "Mengenal proses fotosintesis dan respirasi sel."),
        LearningVideo(4, "Grammar: Simple Present", "Bahasa Inggris", "SMP/SMA", "Materi StudyMate", "8 menit", "Penggunaan simple present dalam kalimat sehari-hari."),
        LearningVideo(5, "Pergerakan Nasional Indonesia", "Sejarah", "SMA", "Materi StudyMate", "11 menit", "Gambaran organisasi dan peristiwa penting pergerakan nasional.")
    )

    private val _notifications = MutableStateFlow(
        listOf(
            StudyNotification(1, "Target belajar hari ini", "Selesaikan satu sesi fokus untuk menjaga rutinitas belajarmu.", "Hari ini", false),
            StudyNotification(2, "Jadwal belajar", "Jangan lupa meninjau jadwal belajar yang sudah kamu buat.", "Kemarin", false),
            StudyNotification(3, "Selamat datang di StudyMate", "Catat materi, atur jadwal, dan mulai sesi fokus pertamamu.", "Baru-baru ini", true)
        )
    )
    override val notifications: StateFlow<List<StudyNotification>> = _notifications.asStateFlow()

    override fun findNotification(id: Int): StudyNotification? = _notifications.value.find { it.id == id }

    override fun markNotificationRead(id: Int) {
        _notifications.value = _notifications.value.map { item ->
            if (item.id == id) item.copy(isRead = true) else item
        }
    }

    override fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }
}
