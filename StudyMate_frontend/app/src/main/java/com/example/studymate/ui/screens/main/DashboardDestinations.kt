@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)

package com.example.studymate.ui.screens.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.studymate.ui.components.StudyMateBackHeader
import com.example.studymate.ui.components.SearchBar
import com.example.studymate.viewmodel.DashboardResultType
import com.example.studymate.viewmodel.DashboardSearchResult
import com.example.studymate.viewmodel.DashboardNewsItem
import com.example.studymate.viewmodel.DashboardViewModel
import java.net.URI
import com.example.studymate.ui.localization.localizedUiLabel
import com.example.studymate.ui.localization.LocalAppLanguage
import com.example.studymate.ui.localization.appString
import com.example.studymate.ui.localization.localizedFormat

@Composable
internal fun LearningProgressScreen(
    navController: NavHostController,
    scheduleViewModel: com.example.studymate.ui.screens.schedule.ScheduleViewModel,
    notesViewModel: com.example.studymate.ui.screens.notes.NotesViewModel,
    focusTimerViewModel: FocusTimerViewModel,
    goalsViewModel: LearningGoalsViewModel
) {
    val language = LocalAppLanguage.current
    val schedules by scheduleViewModel.fullSchedules.collectAsState()
    val notes by notesViewModel.fullNotes.collectAsState()
    val focus by focusTimerViewModel.state.collectAsState()
    val goals by goalsViewModel.goals.collectAsState()
    val awards by goalsViewModel.awards.collectAsState()
    val metrics = remember(schedules, notes, focus.completedFocusSessions) {
        LearningProgressMetrics.from(schedules, notes, focus.completedFocusSessions)
    }
    val snackbarState = remember { SnackbarHostState() }
    var hasLoadedAwards by remember { mutableStateOf(false) }

    LaunchedEffect(metrics, goals) {
        goalsViewModel.refreshAchievements(metrics)
    }
    LaunchedEffect(awards) {
        if (hasLoadedAwards && awards.isNotEmpty()) {
            snackbarState.showSnackbar(appString(language, "goal_achievement_snackbar"))
        }
        hasLoadedAwards = true
    }
    val activeGoals = goals.filter { it.completedAtMillis == null }
    val completedGoals = goals.filter { it.completedAtMillis != null }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 2.dp
            ) {
                DestinationTopBar("Progress Belajar", navController)
            }
        },
        snackbarHost = { SnackbarHost(snackbarState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { insets ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = insets.calculateTopPadding() + 8.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SchoolTaskSummaryCard(metrics) {
                    navController.navigate("add_schedule")
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(localizedUiLabel("Ringkasan aktivitas"), modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("${metrics.eligibleSchedules} ${localizedUiLabel("Aktivitas terjadwal")}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProgressMetric("Aktivitas selesai", metrics.completedActivities.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
                        ProgressMetric("Aktivitas belum selesai", metrics.remainingActivities.toString(), Icons.Default.CalendarToday, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProgressMetric("Total catatan", metrics.noteCount.toString(), Icons.Default.MenuBook, Modifier.weight(1f))
                        ProgressMetric("Sesi Fokus selesai", metrics.focusSessions.toString(), Icons.Default.PlayCircle, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProgressMetric("Aktivitas terjadwal", metrics.eligibleSchedules.toString(), Icons.Default.CalendarToday, Modifier.weight(1f))
                        ProgressMetric("Durasi Fokus", "${metrics.focusDurationMinutes} mnt", Icons.Default.TrendingUp, Modifier.weight(1f))
                    }
                }
            }
            item {
                Text(localizedUiLabel("Riwayat Fokus Belajar"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            if (focus.completedFocusSessions.isEmpty()) {
                item {
                    Text(
                        localizedUiLabel("Sesi akan tercatat setelah timer mode Fokus selesai sepenuhnya. Timer biasa dan sesi yang dibatalkan tidak dihitung."),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(focus.completedFocusSessions.sortedByDescending { it.completedAtMillis }, key = { "focus-${it.id}" }) { session ->
                    FocusSessionCard(session)
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(localizedUiLabel("Target belajar"), modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = { navController.navigate("add_learning_goal") }) { Text(localizedUiLabel("Tambah Target")) }
                }
            }
            if (activeGoals.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(localizedUiLabel("Belum ada target aktif"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(
                                localizedUiLabel("Progres target dihitung otomatis dari tugas sekolah, catatan, atau sesi Fokus yang benar-benar selesai."),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(onClick = { navController.navigate("add_learning_goal") }) { Text(localizedUiLabel("Buat Target")) }
                        }
                    }
                }
            } else {
                items(activeGoals, key = { it.id }) { goal ->
                    LearningGoalProgressCard(
                        goal = goal,
                        metrics = metrics,
                        onEdit = { navController.navigate("edit_learning_goal/${goal.id}") },
                        onDelete = { goalsViewModel.deleteGoal(goal.id) }
                    )
                }
            }
            if (completedGoals.isNotEmpty()) {
                item {
                    Text(localizedUiLabel("Target selesai"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                items(completedGoals, key = { "completed-${it.id}" }) { goal ->
                    LearningGoalProgressCard(
                        goal = goal,
                        metrics = metrics,
                        onEdit = { navController.navigate("edit_learning_goal/${goal.id}") },
                        onDelete = { goalsViewModel.deleteGoal(goal.id) }
                    )
                }
            }
            item {
                Text(localizedUiLabel("Riwayat pencapaian"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            if (awards.isEmpty()) {
                item {
                    Text(
                        localizedUiLabel("Penghargaan akan muncul setelah target belajar berhasil dicapai."),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                items(awards.sortedByDescending { it.earnedAtMillis }, key = { it.id }) { award ->
                    AchievementCard(award)
                }
            }
        }
    }
}

@Composable
private fun SchoolTaskSummaryCard(metrics: LearningProgressMetrics, onAddTask: () -> Unit) {
    val fraction = metrics.schoolTaskCompletionPercent / 100f
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    localizedUiLabel("Progres tugas sekolah"),
                    modifier = Modifier.weight(1f).padding(start = 10.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${metrics.schoolTaskCompletionPercent}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
            Text(
                String.format(
                    java.util.Locale.forLanguageTag(com.example.studymate.ui.localization.LocalAppLanguage.current),
                    com.example.studymate.ui.localization.appString(com.example.studymate.ui.localization.LocalAppLanguage.current, "school_task_count_summary"),
                    metrics.completedSchoolTasks,
                    metrics.schoolTaskCount,
                    metrics.remainingSchoolTasks
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                if (metrics.schoolTaskCount == 0) {
                    localizedUiLabel("Buat jadwal dengan kategori Tugas Sekolah untuk mulai mencatat tugas.")
                } else {
                    localizedUiLabel("Hanya jadwal berkategori Tugas Sekolah yang dihitung di sini. Tandai selesai dari menu jadwal.")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (metrics.schoolTaskCount == 0) {
                TextButton(onClick = onAddTask) { Text(localizedUiLabel("Tambah Tugas Sekolah")) }
            }
        }
    }
}

@Composable
private fun FocusSessionCard(session: FocusSession) {
    val language = LocalAppLanguage.current
    val date = remember(session.completedAtMillis, language) {
        java.text.SimpleDateFormat("d MMM yyyy, HH:mm", java.util.Locale.forLanguageTag(language))
            .format(java.util.Date(session.completedAtMillis))
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Text(date, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(
                    String.format(
                        java.util.Locale.forLanguageTag(language),
                        appString(language, "focus_duration_completed"),
                        session.durationMillis / 60_000L
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LearningGoalProgressCard(
    goal: LearningGoal,
    metrics: LearningProgressMetrics,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val language = LocalAppLanguage.current
    val current = metrics.valueFor(goal.type).coerceAtLeast(0)
    val isCompleted = goal.completedAtMillis != null
    val displayedCurrent = if (isCompleted) goal.targetValue else current
    val fraction = if (isCompleted) 1f else
        (current.toFloat() / goal.targetValue.coerceAtLeast(1)).coerceIn(0f, 1f)
    var menuExpanded by remember(goal.id) { mutableStateOf(false) }
    var confirmDelete by remember(goal.id) { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(goal.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${(fraction * 100).toInt()}%", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Text(localizedUiLabel(goal.type.label), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    String.format(
                        java.util.Locale.forLanguageTag(language),
                        appString(language, if (isCompleted) "goal_progress_done" else "goal_progress_remaining"),
                        displayedCurrent,
                        goal.targetValue,
                        localizedUiLabel(goal.type.unit),
                        (goal.targetValue - current).coerceAtLeast(0)
                    ).let { if (isCompleted) "$it · ${appString(language, "done")}" else it },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box {
                    IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = localizedFormat(
                                com.example.studymate.ui.localization.LocalAppLanguage.current,
                                "manage_goal",
                                goal.name
                            )
                        )
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(localizedUiLabel("Edit")) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(localizedUiLabel("Hapus")) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                confirmDelete = true
                            }
                        )
                    }
                }
            }
            if (isCompleted) {
                Text(localizedUiLabel("Target ini telah diselesaikan dan penghargaan tersimpan."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            if (goal.description.isNotBlank()) {
                Text(goal.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (confirmDelete) {
            AlertDialog(
                onDismissRequest = { confirmDelete = false },
                title = { Text(localizedUiLabel("Hapus target?")) },
                text = {
                    Text(
                        String.format(
                            java.util.Locale.forLanguageTag(language),
                            appString(language, "delete_goal_description"),
                            goal.name
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        confirmDelete = false
                        onDelete()
                    }) { Text(localizedUiLabel("Hapus"), color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(localizedUiLabel("Batal")) } }
            )
        }
    }
}

@Composable
private fun AchievementCard(award: LearningAward) {
    val language = LocalAppLanguage.current
    val date = remember(award.earnedAtMillis, language) {
        java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.forLanguageTag(language))
            .format(java.util.Date(award.earnedAtMillis))
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(award.goalName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    String.format(
                        java.util.Locale.forLanguageTag(language),
                        appString(language, "award_earned_date"),
                        localizedUiLabel(award.goalType.label),
                        date
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProgressMetric(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
            Text(localizedUiLabel(label), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun TutoringCentersScreen(navController: NavHostController, viewModel: DashboardViewModel) {
    var query by remember { mutableStateOf("") }
    var selectedFilters by remember {
        mutableStateOf(
            mapOf(
                "province" to "Semua Provinsi",
                "location" to "Semua",
                "education" to "Semua",
                "subject" to "Semua"
            )
        )
    }
    val tutoringCategories = listOf("Semua", "Matematika", "Fisika dan Kimia", "Bahasa Inggris", "Persiapan UTBK")
    val tutoringProvinces = viewModel.tutoringCenters.mapNotNull { it.province?.takeIf(String::isNotBlank) }.distinct()
    val tutoringLocations = viewModel.tutoringCenters.map { it.city?.takeIf(String::isNotBlank) ?: it.location }.distinct()
    val tutoringEducationLevels = viewModel.tutoringCenters
        .flatMap { it.educationLevel.orEmpty().split(Regex("[/,–-]")) }
        .map(String::trim)
        .filter { it in educationLevels }
        .distinct()
    val filtered = viewModel.filterTutoringCenters(
        query = query,
        location = selectedFilters.getValue("location"),
        subject = selectedFilters.getValue("subject"),
        province = selectedFilters.getValue("province"),
        educationLevel = selectedFilters.getValue("education")
    )

    Scaffold(
        topBar = {
            DestinationTopBar("Info Bimbel", navController) {
                FilterPopupButton(
                    groups = listOf(
                        FilterPopupGroup(
                            key = "province",
                            title = "Provinsi",
                            options = listOf("Semua Provinsi") + tutoringProvinces,
                            defaultOption = "Semua Provinsi",
                            resetOnSelection = mapOf("location" to "Semua"),
                            unavailableMessage = "Data contoh belum mencatat provinsi, jadi filter provinsi belum tersedia."
                                .takeIf { tutoringProvinces.isEmpty() }
                        ),
                        FilterPopupGroup(
                            key = "location",
                            title = "Kota/Kabupaten",
                            options = listOf("Semua") + tutoringLocations,
                            defaultOption = "Semua",
                            optionsForSelection = { draftFilters ->
                                val province = draftFilters["province"] ?: "Semua Provinsi"
                                val cities = viewModel.tutoringCenters
                                    .filter { province == "Semua Provinsi" || it.province == province }
                                    .map { it.city?.takeIf(String::isNotBlank) ?: it.location }
                                    .distinct()
                                listOf("Semua") + cities
                            },
                        ),
                        FilterPopupGroup(
                            key = "education",
                            title = "Jenjang Pendidikan",
                            options = educationLevels,
                            defaultOption = "Semua",
                            enabledOptions = setOf("Semua") + tutoringEducationLevels,
                            unavailableMessage = "Data contoh bimbel belum mencatat jenjang pendidikan."
                                .takeIf { tutoringEducationLevels.isEmpty() }
                        ),
                        FilterPopupGroup(
                            key = "subject",
                            title = "Mata Pelajaran",
                            options = tutoringCategories,
                            defaultOption = "Semua"
                        )
                    ),
                    selected = selectedFilters,
                    onApply = { selectedFilters = it }
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(top = 8.dp)
        ) {
            Text(
                localizedUiLabel("Contoh data lokal untuk pratinjau."),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = "Cari bimbel...",
                modifier = Modifier.padding(top = 8.dp),
                compact = true
            )
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filtered.isEmpty()) {
                    item { EmptyDestination(localizedUiLabel("Bimbel tidak ditemukan"), localizedUiLabel("Coba kata kunci atau kategori lain.")) }
                } else {
                    items(filtered, key = { it.id }) { center ->
                        Card(
                            onClick = { navController.navigate("tutoring_detail/${center.id}") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TutoringCenterImage(
                                    center = center,
                                    modifier = Modifier.size(width = 84.dp, height = 76.dp),
                                    placeholderIconSize = 32.dp
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(
                                    Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        center.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        center.category,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    TutoringInfoBadge(
                                        icon = Icons.Default.School,
                                        text = center.educationLevel ?: "Jenjang belum tersedia"
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(Modifier.width(3.dp))
                                        Text(
                                            tutoringCenterLocation(center),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = localizedFormat(
                                        com.example.studymate.ui.localization.LocalAppLanguage.current,
                                        "detail_item",
                                        center.name
                                    ),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TutoringCenterDetailScreen(navController: NavHostController, viewModel: DashboardViewModel, centerId: Int) {
    val center = viewModel.findTutoringCenter(centerId)
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val dialNumber = center?.contactNumber?.let(::normalizedContactNumber)
    val whatsappNumber = center?.contactNumber?.let(::whatsAppNumber)
    val website = center?.website?.takeIf(::isValidHttpUrl)
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                DestinationTopBar("Detail Bimbel", navController)
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { insets ->
        if (center == null) {
            Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.Center) {
                EmptyDestination(localizedUiLabel("Bimbel tidak ditemukan"), localizedUiLabel("Data contoh ini tidak tersedia."))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(insets),
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    TutoringCenterImage(
                        center = center,
                        modifier = Modifier.fillMaxWidth().height(190.dp),
                        placeholderIconSize = 60.dp
                    )
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TutoringCenterImage(
                                center = center,
                                modifier = Modifier.size(72.dp),
                                placeholderIconSize = 36.dp
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Text(
                                    center.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    center.category,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TutoringInfoBadge(
                                        icon = Icons.Default.School,
                                        text = center.educationLevel ?: "Jenjang belum tersedia"
                                    )
                                    TutoringInfoBadge(
                                        icon = Icons.Default.LocationOn,
                                        text = tutoringCenterLocation(center)
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TutoringSectionTitle("Alamat")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 2.dp).size(20.dp)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    center.address?.takeIf(String::isNotBlank)
                                        ?: tutoringCenterLocation(center),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (center.address.isNullOrBlank()) {
                                    Text(
                                        localizedUiLabel("Alamat lengkap belum tersedia pada data contoh."),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TutoringSectionTitle("Tentang Kami")
                        Text(
                            center.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (center.sampleFee != null) {
                    item { DetailLine("Biaya", center.sampleFee) }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (whatsappNumber != null) {
                            Button(
                                onClick = { uriHandler.openUri("https://wa.me/$whatsappNumber") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = androidx.compose.ui.graphics.Color(0xFF25D366),
                                    contentColor = androidx.compose.ui.graphics.Color.White
                                )
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(localizedUiLabel("Hubungi via WhatsApp"))
                            }
                        }
                        if (dialNumber != null || website != null) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                if (dialNumber != null) {
                                    OutlinedButton(
                                        onClick = {
                                            context.startActivity(
                                                Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", dialNumber, null))
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text(localizedUiLabel("Telepon"), maxLines = 1)
                                    }
                                }
                                if (website != null) {
                                    OutlinedButton(
                                        onClick = { uriHandler.openUri(website) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(Icons.Default.Language, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text(localizedUiLabel("Kunjungi Website"), maxLines = 1)
                                    }
                                }
                            }
                        }
                        if (dialNumber == null && website == null) {
                            Text(
                                localizedUiLabel("Informasi kontak belum tersedia pada data contoh."),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            localizedUiLabel("Informasi ini merupakan data contoh, bukan penawaran terverifikasi."),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TutoringCenterImage(
    center: com.example.studymate.viewmodel.TutoringCenter,
    modifier: Modifier,
    placeholderIconSize: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.LocationCity,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(placeholderIconSize)
        )
        center.imageUrl
            ?.takeIf(::isValidHttpUrl)
            ?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = center.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
    }
}

@Composable
private fun TutoringInfoBadge(icon: ImageVector, text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TutoringSectionTitle(title: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(4.dp)
                .height(22.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            localizedUiLabel(title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun tutoringCenterLocation(center: com.example.studymate.viewmodel.TutoringCenter): String {
    val cityOrLocation = center.city?.takeIf(String::isNotBlank) ?: center.location
    return listOf(cityOrLocation, center.province?.takeIf(String::isNotBlank))
        .filterNotNull()
        .joinToString(", ")
}

private fun normalizedContactNumber(value: String?): String? {
    val normalized = value.orEmpty().filter { it.isDigit() || it == '+' }
    return normalized.takeIf { it.count(Char::isDigit) in 7..15 }
}

private fun whatsAppNumber(value: String?): String? {
    val digits = normalizedContactNumber(value)?.filter(Char::isDigit) ?: return null
    val international = when {
        digits.startsWith("00") -> digits.drop(2)
        digits.startsWith("0") -> "62${digits.drop(1)}"
        else -> digits
    }
    return international.takeIf { it.length in 7..15 }
}

@Composable
fun LearningVideosScreen(navController: NavHostController, viewModel: DashboardViewModel) {
    var query by remember { mutableStateOf("") }
    var selectedFilters by remember {
        mutableStateOf(mapOf("education" to "Semua", "subject" to "Semua"))
    }
    val filtered = viewModel.filterLearningVideos(
        query = query,
        educationLevel = selectedFilters.getValue("education"),
        subject = selectedFilters.getValue("subject")
    )

    Scaffold(
        topBar = {
            DestinationTopBar("Materi Video", navController) {
                FilterPopupButton(
                    groups = listOf(
                        FilterPopupGroup("education", "Jenjang Pendidikan", educationLevels, "Semua"),
                        FilterPopupGroup(
                            "subject",
                            "Mata Pelajaran",
                            listOf("Semua", "Matematika", "Fisika dan Kimia", "Bahasa Inggris", "Persiapan UTBK"),
                            "Semua"
                        )
                    ),
                    selected = selectedFilters,
                    onApply = { selectedFilters = it }
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(start = 16.dp, top = 6.dp, end = 16.dp)
        ) {
            OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), label = { Text(localizedUiLabel("Cari materi")) }, singleLine = true, shape = RoundedCornerShape(14.dp))
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filtered.isEmpty()) {
                    item { EmptyDestination(localizedUiLabel("Materi tidak ditemukan"), localizedUiLabel("Coba kata kunci atau mata pelajaran lain.")) }
                } else {
                    items(filtered, key = { it.id }) { video ->
                        Card(
                            onClick = { navController.navigate("video_detail/${video.id}") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.width(124.dp).aspectRatio(16f / 9f).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val thumbnailUrl = remember(video.videoUrl) { youtubeThumbnailUrl(video.videoUrl) }
                                    if (thumbnailUrl != null) {
                                        AsyncImage(
                                            model = thumbnailUrl,
                                            contentDescription = "Thumbnail ${video.title}",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(42.dp))
                                    }
                                    Surface(
                                        modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.inverseSurface,
                                        contentColor = MaterialTheme.colorScheme.inverseOnSurface
                                    ) {
                                        Text(video.duration, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(video.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Text("${video.subject} · ${video.educationLevel}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(video.duration, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private val educationLevels = listOf("Semua", "SD", "SMP", "SMA", "SMK", "Kuliah")

private data class FilterPopupGroup(
    val key: String,
    val title: String,
    val options: List<String>,
    val defaultOption: String,
    val enabledOptions: Set<String>? = null,
    val unavailableMessage: String? = null,
    val optionsForSelection: ((Map<String, String>) -> List<String>)? = null,
    val resetOnSelection: Map<String, String> = emptyMap()
)

@Composable
private fun FilterPopupButton(
    groups: List<FilterPopupGroup>,
    selected: Map<String, String>,
    onApply: (Map<String, String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf(selected) }
    val activeCount = groups.count { selected[it.key] != it.defaultOption }

    Box {
        IconButton(
            onClick = {
                if (expanded) {
                    expanded = false
                } else {
                    draft = selected
                    expanded = true
                }
            },
            modifier = Modifier.size(48.dp)
        ) {
            Box {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = localizedUiLabel("Filter"),
                    tint = if (activeCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (activeCount > 0) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                draft = selected
            },
            modifier = Modifier.widthIn(min = 280.dp, max = 360.dp).heightIn(max = 560.dp)
        ) {
            Column(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    localizedUiLabel("Filter"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                groups.forEach { group ->
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(group.title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                        val options = group.optionsForSelection?.invoke(draft) ?: group.options
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            options.forEach { option ->
                                val enabled = group.enabledOptions?.contains(option) ?: true
                                FilterChip(
                                    selected = draft[group.key] == option,
                                    onClick = {
                                        if (enabled) {
                                            draft = (draft + (group.key to option)) + group.resetOnSelection
                                        }
                                    },
                                    enabled = enabled,
                                    label = { Text(option) }
                                )
                            }
                        }
                        group.unavailableMessage?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            draft = groups.associate { it.key to it.defaultOption }
                        }
                    ) { Text(localizedUiLabel("Reset")) }
                    TextButton(
                        onClick = {
                            onApply(draft)
                            expanded = false
                        }
                    ) { Text(localizedUiLabel("Terapkan")) }
                }
            }
        }
    }
}

private fun youtubeThumbnailUrl(videoUrl: String?): String? {
    val uri = runCatching { URI(videoUrl ?: return null) }.getOrNull() ?: return null
    val host = uri.host?.lowercase() ?: return null
    val allowedHosts = setOf("youtube.com", "www.youtube.com", "m.youtube.com", "music.youtube.com", "youtube-nocookie.com", "www.youtube-nocookie.com", "youtu.be", "www.youtu.be")
    if (host !in allowedHosts) return null

    val segments = uri.path.orEmpty().split('/').filter(String::isNotBlank)
    val videoId = when {
        host == "youtu.be" || host == "www.youtu.be" -> segments.firstOrNull()
        segments.firstOrNull() == "watch" -> uri.query.orEmpty().split('&').firstOrNull { it.startsWith("v=") }?.substringAfter('=')
        segments.firstOrNull() in setOf("embed", "shorts", "live") -> segments.getOrNull(1)
        else -> null
    }
    return videoId?.takeIf { it.matches(Regex("[A-Za-z0-9_-]{11}")) }
        ?.let { "https://img.youtube.com/vi/$it/hqdefault.jpg" }
}

@Composable
fun LearningVideoDetailScreen(navController: NavHostController, viewModel: DashboardViewModel, videoId: Int) {
    val video = viewModel.findLearningVideo(videoId)
    val uriHandler = LocalUriHandler.current
    Scaffold(topBar = {
        Surface(color = MaterialTheme.colorScheme.surface) {
            DestinationTopBar("Detail Materi", navController)
        }
    }) { insets ->
        if (video == null) {
            Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.Center) {
                EmptyDestination(localizedUiLabel("Materi tidak ditemukan"), localizedUiLabel("Data contoh ini tidak tersedia."))
            }
        } else {
            val validUrl = video.videoUrl?.takeIf(::isValidHttpUrl)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, top = insets.calculateTopPadding() + 12.dp, end = 20.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(68.dp))
                    }
                }
                item { Text(video.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                item { DetailLine("Mata pelajaran", video.subject) }
                item { DetailLine("Tingkat", video.educationLevel) }
                item { DetailLine("Sumber", video.sourceName) }
                item { DetailLine("Durasi", video.duration) }
                item { Text(video.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                item {
                    if (validUrl != null) {
                        Button(onClick = { uriHandler.openUri(validUrl) }, modifier = Modifier.fillMaxWidth()) { Text(localizedUiLabel("Buka Video")) }
                    } else {
                        Text(localizedUiLabel("URL video belum tersedia untuk materi contoh ini."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun NewsListScreen(navController: NavHostController, viewModel: DashboardViewModel) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, viewModel) { viewModel.filterNews(query) }

    Scaffold(topBar = { DestinationTopBar("Berita Pendidikan", navController) }) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = insets.calculateTopPadding() + 8.dp, end = 16.dp)
        ) {
            OutlinedTextField(
                query,
                { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(localizedUiLabel("Cari berita")) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
            if (filtered.isEmpty()) {
                item { EmptyDestination(localizedUiLabel("Berita tidak ditemukan"), localizedUiLabel("Coba kata kunci atau kategori lain.")) }
            } else {
                items(filtered, key = { it.id }) { news ->
                    NewsItemCard(news = news, onClick = { navController.navigate("news_detail/${news.id}") })
                }
            }
            }
        }
    }
}

@Composable
fun NewsDetailScreen(navController: NavHostController, viewModel: DashboardViewModel, newsId: Int) {
    val news = viewModel.findNews(newsId)
    val uriHandler = LocalUriHandler.current
    Scaffold(topBar = {
        Surface(color = MaterialTheme.colorScheme.surface) {
            DestinationTopBar("Detail Berita", navController)
        }
    }) { insets ->
        if (news == null) {
            Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.Center) {
                EmptyDestination(localizedUiLabel("Berita tidak ditemukan"), localizedUiLabel("Data contoh ini tidak tersedia."))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, top = insets.calculateTopPadding() + 12.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        val thumbnailUrl = news.thumbnailUrl
                        if (!thumbnailUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = thumbnailUrl,
                                contentDescription = news.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Article, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp))
                        }
                    }
                }
                item { Text(news.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(news.category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(news.publishedAt, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                news.sourceName?.let { source -> item { DetailLine("Sumber", source) } }
                item { Text(news.summary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium) }
                item { Text(news.content, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                item {
                    val validUrl = news.sourceUrl?.takeIf(::isValidHttpUrl)
                    if (validUrl != null) {
                        Button(onClick = { uriHandler.openUri(validUrl) }) { Text(localizedUiLabel("Buka sumber asli")) }
                    } else {
                        Text(localizedUiLabel("Konten contoh StudyMate; tautan sumber asli tidak tersedia."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotificationsScreen(navController: NavHostController, viewModel: DashboardViewModel) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }
    Scaffold(topBar = { DestinationTopBar("Notifikasi", navController) }) { insets ->
        if (notifications.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.Center) {
                EmptyDestination("Belum ada notifikasi", "Notifikasi StudyMate akan muncul di sini.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = insets.calculateTopPadding() + 4.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (unreadCount > 0) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = viewModel::markAllNotificationsRead) {
                                Text(localizedUiLabel("Tandai semua dibaca"))
                            }
                        }
                    }
                }
                items(notifications, key = { it.id }) { notification ->
                    Card(
                        onClick = {
                            viewModel.markNotificationRead(notification.id)
                            navController.navigate("notification_detail/${notification.id}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Default.NotificationsNone,
                                contentDescription = null,
                                tint = if (notification.isRead) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 2.dp).size(21.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(notification.title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                    if (!notification.isRead) Box(Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                                }
                                Text(notification.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(notification.timeLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationDetailScreen(navController: NavHostController, viewModel: DashboardViewModel, notificationId: Int) {
    val notification = viewModel.findNotification(notificationId)
    Scaffold(topBar = { DestinationTopBar("Detail Notifikasi", navController) }) { insets ->
        if (notification == null) {
            Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.Center) {
                EmptyDestination(localizedUiLabel("Notifikasi tidak ditemukan"), localizedUiLabel("Notifikasi ini tidak tersedia."))
            }
        } else {
            Column(Modifier.fillMaxSize().padding(insets).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                Text(notification.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(notification.timeLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(notification.message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(localizedUiLabel("Dibaca"), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun DestinationTopBar(
    title: String,
    navController: NavHostController,
    actions: @Composable (() -> Unit)? = null
) {
    StudyMateBackHeader(
        title = title,
        onBack = { navController.popBackStack() },
        actions = actions
    )
}

@Composable
internal fun NewsItemCard(news: DashboardNewsItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.width(96.dp).aspectRatio(16f / 9f).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                val thumbnailUrl = news.thumbnailUrl
                if (!thumbnailUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = news.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Article, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(news.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(news.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("${news.category} · ${news.publishedAt}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 1)
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = localizedUiLabel("Buka berita"),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(localizedUiLabel(label), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun EmptyDestination(title: String, message: String) {
    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(localizedUiLabel(title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Text(localizedUiLabel(message), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
    }
}

private fun isValidHttpUrl(value: String): Boolean =
    runCatching { URI(value).let { it.scheme in setOf("http", "https") && !it.host.isNullOrBlank() } }.getOrDefault(false)
