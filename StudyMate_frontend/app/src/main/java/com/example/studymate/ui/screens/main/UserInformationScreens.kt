package com.example.studymate.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.example.studymate.ui.components.StudyMateBackHeader
import com.example.studymate.ui.localization.LocalAppLanguage
import com.example.studymate.ui.localization.localizedInformationBody
import com.example.studymate.ui.localization.localizedInformationTitle
import com.example.studymate.ui.localization.localizedUiLabel

@Composable
private fun InformationPage(
    title: String,
    navController: NavController,
    content: LazyListScopeContent
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        StudyMateBackHeader(
            title = localizedUiLabel(title),
            onBack = {
                if (!navController.popBackStack()) {
                    navController.navigate("user") { launchSingleTop = true }
                }
            }
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            content()
        }
    }
}

private typealias LazyListScopeContent = androidx.compose.foundation.lazy.LazyListScope.() -> Unit

@Composable
fun HelpCenterScreen(navController: NavController) {
    InformationPage("Pusat Bantuan", navController) {
        item { HelpSection("help_welcome", "Selamat Datang di Pusat Bantuan", "Pusat Bantuan StudyMate disediakan untuk membantu Anda memahami fitur yang tersedia dan cara menggunakannya. Anda dapat mempelajari navigasi utama, pengelolaan jadwal, pencatatan materi, penggunaan timer, serta pengaturan profil. Bacalah bagian yang sesuai dengan kebutuhan Anda dan periksa kembali informasi yang dimasukkan sebelum menyimpannya.") }
        item {
            HelpSection(
                "help_navigation",
                "Mengenal Navigasi Utama",
                "Home menjadi halaman utama untuk melihat ringkasan dan akses menuju fitur belajar yang tersedia. Informasi di dalamnya mengikuti data dan fitur yang telah diterapkan pada aplikasi.\n\nSchedule digunakan untuk melihat dan mengelola jadwal. Notes digunakan untuk membuat, membaca, mengedit, mencari, menyematkan, dan menghapus catatan. Focus membantu mengatur sesi belajar menggunakan timer. User menyediakan profil, pengaturan akun, Pusat Bantuan, dan Tentang StudyMate."
            )
        }
        item {
            HelpSection(
                "help_schedule",
                "Cara Menggunakan Fitur Jadwal",
                "1. Buka menu Schedule pada navigasi bawah.\n2. Periksa jadwal yang sudah ditampilkan atau gunakan pencarian jika diperlukan.\n3. Tekan tombol tambah untuk membuat jadwal baru.\n4. Isi judul kegiatan, kategori, tanggal, jam mulai, jam selesai, serta catatan atau pengajar jika diperlukan.\n5. Tekan Simpan, lalu periksa kembali jadwal pada daftar.\n\nPastikan waktu mulai dan selesai sudah benar sebelum menyimpan. StudyMate tidak menjanjikan notifikasi pengingat apabila fitur tersebut belum tersedia."
            )
        }
        item {
            HelpSection(
                "help_notes",
                "Cara Menggunakan Catatan",
                "Buka menu Notes, lalu pilih opsi untuk membuat catatan baru. Masukkan judul yang mudah dikenali dan ketik isi catatan. Editor mendukung daftar bernomor dan bullet untuk membantu menyusun materi. Simpan catatan menggunakan kontrol yang tersedia, kemudian buka kembali catatan untuk membaca atau mengeditnya. Catatan dapat dicari, disematkan, dan dihapus sesuai fitur yang tersedia pada halaman Notes.\n\nGunakan judul yang spesifik dan pisahkan catatan berdasarkan mata pelajaran agar lebih mudah ditemukan."
            )
        }
        item {
            HelpSection(
                "help_timer",
                "Cara Menggunakan Timer Belajar",
                "Buka menu Focus dan gunakan ikon pengaturan timer. Pilih mode Fokus Belajar atau Pengatur Waktu. Pada Pengatur Waktu, atur Jam, Menit, dan Detik, periksa durasi yang ditampilkan, lalu tekan Terapkan. Mulai timer dengan tombol Mulai Timer atau kontrol Start yang tersedia. Saat berjalan, gunakan jeda dan lanjutkan bila diperlukan, atau tekan Stop untuk mengakhiri timer.\n\nJika durasi perlu diubah, lakukan ketika timer tidak sedang berjalan. Timer ini merupakan fitur lokal pada tahap pengembangan; halaman ini tidak menjanjikan notifikasi latar belakang atau riwayat sesi yang belum tersedia."
            )
        }
        item {
            HelpSection(
                "help_profile",
                "Mengelola Profil dan Pengaturan",
                "Menu User menyediakan Edit Profil untuk mengubah nama dan foto profil, serta Akun & Keamanan untuk melihat email, status akun, tanggal bergabung, dan informasi metode login demo. Preferensi Aplikasi menampilkan preferensi yang saat ini tersedia. Pusat Bantuan berisi panduan penggunaan, sedangkan Tentang StudyMate menjelaskan tujuan dan gambaran umum aplikasi.\n\nPerubahan kata sandi atau email belum tersedia pada tahap frontend ini."
            )
        }
        item {
            HelpSection(
                "help_tips",
                "Tips Menggunakan StudyMate dengan Efektif",
                "Buat jadwal yang realistis dan periksa kembali sebelum belajar. Gunakan Notes untuk merangkum materi dan mencatat hal penting. Manfaatkan timer untuk membagi waktu belajar dan istirahat secara teratur. Periksa progres melalui fitur yang memang tersedia, perbarui profil bila diperlukan, dan biasakan memeriksa data sebelum menyimpan."
            )
        }
        item {
            HelpSection(
                "help_faq",
                "Pertanyaan Umum",
                "Bagaimana membuat jadwal baru?\nBuka Schedule, tekan tombol tambah, lengkapi form, lalu simpan.\n\nBagaimana membuat atau mengubah catatan?\nBuka Notes, buat catatan baru atau pilih catatan yang ada, kemudian simpan perubahan.\n\nBagaimana mengatur durasi timer?\nBuka Focus, masuk ke pengaturan timer, pilih Pengatur Waktu, atur tiga kolom durasi, lalu tekan Terapkan.\n\nBagaimana kembali ke halaman sebelumnya?\nGunakan ikon panah pada header halaman.\n\nDi mana informasi tentang StudyMate tersedia?\nBuka menu User, lalu pilih Tentang StudyMate."
            )
        }
        item { HelpSection("help_closing", "Penutup", "StudyMate dirancang sebagai pendamping untuk membantu Anda mengelola aktivitas belajar secara lebih teratur dan praktis. Gunakan fitur yang tersedia sesuai kebutuhan, mulai dari merencanakan jadwal, menyusun catatan, hingga mengatur waktu belajar.") }
    }
}

@Composable
fun AboutStudyMateScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val version = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }.getOrNull() ?: localizedUiLabel("Tidak tersedia")
    InformationPage("Tentang StudyMate", navController) {
        item { HelpSection("about_intro", "Tentang StudyMate", "Teman Belajar untuk Mengelola Aktivitas dan Produktivitas\n\nStudyMate merupakan aplikasi pendamping belajar yang dirancang untuk membantu pelajar mengatur kegiatan belajar dalam satu tempat. Aktivitas belajar tidak hanya berkaitan dengan memahami materi, tetapi juga membutuhkan pengelolaan waktu, pencatatan informasi, penyusunan jadwal, dan evaluasi kebiasaan belajar.") }
        item { HelpSection("about_what", "Apa Itu StudyMate?", "StudyMate dirancang sebagai ruang digital yang menyatukan sejumlah kebutuhan dasar belajar, seperti pengelolaan jadwal, pembuatan dan pengelolaan catatan, penggunaan timer, pengelolaan profil, serta pemantauan progres melalui fitur yang tersedia. Tujuannya adalah mempermudah pengorganisasian aktivitas belajar tanpa mengharuskan pengguna mengandalkan banyak tempat berbeda.\n\nStudyMate tidak menjamin nilai akademik tertentu dan tidak menggantikan guru, materi pembelajaran, atau proses belajar mandiri.") }
        item { HelpSection("about_background", "Latar Belakang dan Tujuan Pengembangan", "StudyMate dikembangkan dari gagasan bahwa pelajar membutuhkan cara yang lebih teratur untuk mengelola aktivitas belajarnya. Jadwal yang tidak tersusun, catatan yang tersebar, dan pembagian waktu yang kurang terencana dapat membuat kegiatan belajar terasa kurang terorganisasi. Karena itu, StudyMate dirancang untuk menyediakan tempat yang membantu pengguna mengatur beberapa kebutuhan belajar sehari-hari secara praktis, dengan navigasi yang sederhana dan konsisten.") }
        item { HelpSection("about_objectives", "Tujuan StudyMate", "Membantu mengatur waktu melalui fitur jadwal dan timer.\n\nMendukung pencatatan materi dengan menyediakan Notes untuk menyimpan rangkuman, ide, dan informasi penting.\n\nMendorong kebiasaan belajar yang lebih teratur melalui perencanaan dan pembagian waktu.\n\nMenyatukan kebutuhan pendukung belajar dalam satu aplikasi.\n\nMemberikan pengalaman yang mudah digunakan melalui antarmuka dan navigasi yang jelas.") }
        item { HelpSection("about_features", "Fitur Utama StudyMate", "Home menjadi pusat informasi dan akses menuju fitur utama serta ringkasan belajar yang tersedia. Schedule mendukung perencanaan kegiatan belajar. Notes membantu menyimpan dan mengatur catatan. Focus mendukung pengelolaan waktu menggunakan timer. User menyediakan akses ke profil dan pengaturan pengguna.\n\nCakupan fungsi mengikuti implementasi aplikasi yang tersedia saat ini. Beberapa bagian masih menggunakan data lokal atau dummy dan belum terhubung ke penyimpanan cloud maupun sinkronisasi online.") }
        item { HelpSection("about_benefits", "Manfaat yang Diharapkan", "StudyMate diharapkan membantu pengguna menyusun kegiatan belajar dengan lebih teratur, mempermudah pengelolaan catatan, mendukung kebiasaan mengalokasikan waktu, dan menyediakan akses ke fitur pendukung belajar dari satu aplikasi. Manfaat ini merupakan dukungan yang ingin diberikan aplikasi, bukan jaminan hasil akademik.") }
        item { HelpSection("about_principles", "Prinsip Pengembangan", "Pengembangan StudyMate diarahkan pada kemudahan penggunaan, tampilan yang konsisten, pengorganisasian aktivitas belajar, pengembangan fitur secara bertahap, dan kejujuran mengenai kemampuan yang benar-benar tersedia. Informasi pada halaman ini tidak mengklaim sertifikasi, standar keamanan, atau integrasi yang belum dapat diverifikasi dari aplikasi.") }
        item { HelpSection("about_status", "Status Pengembangan", "StudyMate masih berada dalam tahap penyempurnaan frontend. Beberapa fitur menggunakan data dummy atau state lokal dan integrasi database/API dapat dilakukan secara bertahap. Versi aplikasi yang tersedia pada konfigurasi saat ini adalah %1\$s.", version) }
        item { HelpSection("about_closing", "Penutup", "StudyMate dirancang sebagai pendamping untuk membantu pelajar mengelola aktivitas belajar secara lebih teratur dan praktis. Gunakan fitur yang tersedia sesuai kebutuhan dan kembangkan kebiasaan belajar yang paling sesuai dengan tujuan Anda.") }
    }
}

@Composable
private fun HelpSection(key: String, title: String, body: String, argument: String? = null) {
    val language = LocalAppLanguage.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(localizedInformationTitle(language, key, title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
        Text(
            text = localizedInformationBody(language, key, body, argument),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Justify,
            lineHeight = 1.5.em
        )
    }
}
