package com.example.studymate.ui.screens.schedule

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import com.example.studymate.ui.components.buildHighlightedText
import com.example.studymate.ui.components.SearchBar
import com.example.studymate.ui.components.StudyMateBackHeader
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationManagerCompat
import com.example.studymate.ui.localization.LocalAppLanguage
import com.example.studymate.ui.localization.localizedCount
import com.example.studymate.ui.localization.localizedUiLabel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleScreen(navController: NavController, viewModel: ScheduleViewModel = viewModel()) {
    val schedules by viewModel.filteredSchedules.collectAsState()
    val query by viewModel.query.collectAsState()
    val groupedSchedules = schedules.groupedFor()
    val todaySchedules = groupedSchedules[ScheduleGroup.TODAY].orEmpty()
    val upcomingSchedules = groupedSchedules[ScheduleGroup.UPCOMING].orEmpty()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var notificationsEnabled by remember {
        mutableStateOf(
            NotificationManagerCompat.from(context).areNotificationsEnabled() &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED)
        )
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled() &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 4.dp, bottom = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                query = query,
                onQueryChange = { q -> viewModel.setQuery(q) },
                placeholder = "Cari jadwal...",
                compact = true
            )
            if (!notificationsEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            localizedUiLabel("Izinkan notifikasi agar pengingat jadwal dan tugas dapat ditampilkan."),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                context.startActivity(
                                    Intent(
                                        Settings.ACTION_APP_NOTIFICATION_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null)
                                    )
                                )
                            }
                        }) { Text(localizedUiLabel("Aktifkan")) }
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    },
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TextButton(
                        onClick = { navController.navigate("previous_schedules") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = localizedUiLabel("JADWAL SEBELUMNYA"),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = localizedUiLabel("Buka jadwal sebelumnya"),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                item {
                    ScheduleSectionHeading("JADWAL HARI INI", topPadding = 0.dp)
                }
                if (todaySchedules.isEmpty()) {
                    item {
                        ScheduleEmptyState(
                            "Belum ada jadwal hari ini",
                            "Gunakan tombol + untuk menambahkan aktivitas."
                        )
                    }
                } else {
                    items(todaySchedules, key = { "today-${it.id}" }) { schedule ->
                        ScheduleCard(
                            schedule = schedule,
                            onClick = { navController.navigate("schedule_detail/${schedule.id}") },
                            onLongPress = { viewModel.deleteSchedule(schedule.id) },
                            onEdit = { navController.navigate("edit_schedule/${schedule.id}") },
                            onDelete = { viewModel.deleteSchedule(schedule.id) },
                            onToggleComplete = { viewModel.setScheduleCompleted(schedule.id, !schedule.isCompleted) },
                            query = query
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
                item {
                    ScheduleSectionHeading("JADWAL YANG AKAN DATANG", topPadding = 0.dp)
                }
                if (upcomingSchedules.isEmpty()) {
                    item {
                        ScheduleEmptyState("Belum ada jadwal mendatang", "Jadwal baru akan muncul di sini.")
                    }
                } else {
                    items(upcomingSchedules, key = { "upcoming-${it.id}" }) { schedule ->
                        ScheduleCard(
                            schedule = schedule,
                            onClick = { navController.navigate("schedule_detail/${schedule.id}") },
                            onLongPress = { viewModel.deleteSchedule(schedule.id) },
                            onEdit = { navController.navigate("edit_schedule/${schedule.id}") },
                            onDelete = { viewModel.deleteSchedule(schedule.id) },
                            onToggleComplete = { viewModel.setScheduleCompleted(schedule.id, !schedule.isCompleted) },
                            query = query
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("add_schedule") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp, pressedElevation = 5.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = localizedUiLabel("Tambah Jadwal"))
        }
    }
}

@Composable
fun ScheduleListScreen(
    schedules: List<Schedule>,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    query: String = "",
    modifier: Modifier = Modifier
) {
    val grouped = schedules.groupBy { it.date }.toSortedMap()

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 2.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        grouped.forEach { (date, items) ->
            item {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
                )
            }
            items(items, key = { it.id }) { s ->
                ScheduleCard(
                    schedule = s,
                    onClick = { onEdit(s.id) },
                    onLongPress = { onDelete(s.id) },
                    onEdit = { onEdit(s.id) },
                    onDelete = { onDelete(s.id) },
                    query = query
                )
            }
        }
        if (schedules.isEmpty()) {
            item {
                Text(
                    text = localizedUiLabel(
                        if (query.isBlank()) "Belum ada jadwal." else "Jadwal tidak ditemukan."
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 36.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleCard(
    schedule: Schedule,
    onClick: (() -> Unit)?,
    onLongPress: (() -> Unit)?,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onToggleComplete: (() -> Unit)? = null,
    query: String = "",
    isPrevious: Boolean = false,
    selected: Boolean = false
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var deleteConfirmation by remember { mutableStateOf(false) }
    val displayedTimes = listOf(schedule.startTime, schedule.endTime)
        .filter { it.isNotBlank() && !(schedule.subject == "Tugas Sekolah" && it == "00:00") }
    val hasTime = displayedTimes.isNotEmpty()
    val timeRange = displayedTimes
        .joinToString(" – ")
    val cardContainerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        label = "scheduleCardSelectionBackground"
    )
    val selectionBorderColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary,
        label = "scheduleCardSelectionBorderColor"
    )
    val selectionBorderWidth by animateDpAsState(
        targetValue = if (selected) 2.dp else 0.dp,
        label = "scheduleCardSelectionBorderWidth"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null || onLongPress != null) {
                    Modifier.combinedClickable(
                        onClick = { onClick?.invoke() },
                        onLongClick = onLongPress
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardContainerColor),
            border = if (selectionBorderWidth > 0.dp) BorderStroke(selectionBorderWidth, selectionBorderColor) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = buildHighlightedText(schedule.title, query),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (hasTime) {
                    Text(
                        timeRange,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = localizedUiLabel("Menu jadwal"),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        if (!isPrevious && onToggleComplete != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(localizedUiLabel(if (schedule.isCompleted) "Batalkan Selesai" else "Tandai Selesai"))
                                },
                                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onToggleComplete()
                                }
                            )
                        }
                        if (!isPrevious && onEdit != null) {
                            DropdownMenuItem(
                                text = { Text(localizedUiLabel("Edit Jadwal")) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onEdit()
                                }
                            )
                        }
                        if (onDelete != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        localizedUiLabel("Hapus Jadwal"),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    deleteConfirmation = true
                                }
                            )
                        }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = categoryIcon(schedule.subject),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buildHighlightedText(localizedUiLabel(schedule.subject), query),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (schedule.teacher.isNotBlank() && schedule.subject != "Tugas Sekolah" && schedule.subject != "Rumah") {
                Text(
                    text = "${localizedUiLabel("Pengajar")}: ${schedule.teacher}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    localizedUiLabel(if (schedule.subject == "Tugas Sekolah") "Deadline" else "Tanggal"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    schedule.date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (schedule.note.isNotBlank()) {
                Text(
                    text = buildHighlightedText(schedule.note, query),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (schedule.isCompleted) {
                Text(
                    text = localizedUiLabel("Selesai"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    if (deleteConfirmation) {
        AlertDialog(
            onDismissRequest = { deleteConfirmation = false },
            title = {
                Text(
                    localizedUiLabel(
                        if (isPrevious) "Hapus jadwal dari riwayat?" else "Hapus jadwal?"
                    )
                )
            },
            text = {
                Text(
                    localizedUiLabel(
                        if (isPrevious) "Jadwal ini akan dihapus dari riwayat."
                        else "Jadwal ini akan dihapus dari daftar."
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    deleteConfirmation = false
                    onDelete?.invoke()
                }) { Text(localizedUiLabel("Hapus")) }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmation = false }) { Text(localizedUiLabel("Batal")) }
            }
        )
    }
}


@Composable
private fun ScheduleSectionHeading(text: String, topPadding: androidx.compose.ui.unit.Dp = 8.dp) {
    Text(
        text = localizedUiLabel(text),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = topPadding, bottom = 6.dp)
    )
}

@Composable
private fun ScheduleEmptyState(title: String, message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
            Text(localizedUiLabel(title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                text = localizedUiLabel(message),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun categoryIcon(category: String): ImageVector {
    val value = category.lowercase()
    return when {
        value.contains("gym") || value.contains("olahraga") || value.contains("jogging") -> Icons.Default.FitnessCenter
        value.contains("rumah") || value.contains("pribadi") -> Icons.Default.Home
        value.contains("organisasi") || value.contains("rapat") -> Icons.Default.Groups
        value.contains("sekolah") || value.contains("matematika") || value.contains("fisika") || value.contains("kimia") -> Icons.Default.School
        value.contains("belajar") || value.contains("les") -> Icons.Default.MenuBook
        else -> Icons.Default.EventAvailable
    }
}

@Composable
fun PreviousSchedulesScreen(
    navController: NavController,
    viewModel: ScheduleViewModel = viewModel()
) {
    val schedules by viewModel.filteredSchedules.collectAsState()
    val todayStart = remember { startOfTodayMillis() }
    val previous = schedules.groupedFor(todayStart)[ScheduleGroup.PREVIOUS].orEmpty()
    val previousIds = remember(previous) { previous.mapTo(mutableSetOf()) { it.id } }
    var selectedScheduleIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val isSelectionMode = selectedScheduleIds.isNotEmpty()
    val todayCalendar = java.util.Calendar.getInstance().apply { timeInMillis = todayStart }

    LaunchedEffect(previousIds) {
        selectedScheduleIds = selectedScheduleIds.intersect(previousIds)
        if (selectedScheduleIds.isEmpty()) showDeleteConfirmation = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        StudyMateBackHeader(
            title = if (isSelectionMode) localizedCount("selected_schedules", selectedScheduleIds.size) else "Jadwal Sebelumnya",
            onBack = { navController.popBackStack() },
            actions = if (isSelectionMode) {
                {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(Icons.Default.Delete, contentDescription = localizedUiLabel("Hapus jadwal yang dipilih"))
                    }
                    IconButton(onClick = { selectedScheduleIds = emptySet() }) {
                        Icon(Icons.Default.Close, contentDescription = localizedUiLabel("Batalkan pemilihan"))
                    }
                }
            } else null
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val groups = listOf(
                "KEMARIN" to previous.filter { dayDifference(todayStart, it.date) == 1 },
                "MINGGU LALU" to previous.filter { dayDifference(todayStart, it.date) in 2..7 },
                "BULAN LALU" to previous.filter { dayDifference(todayStart, it.date) in 8..31 },
                "TAHUN INI" to previous.filter {
                    val millis = scheduleDateMillis(it.date) ?: Long.MAX_VALUE
                    dayDifference(todayStart, it.date) > 31 &&
                        java.util.Calendar.getInstance().apply { timeInMillis = millis }.get(java.util.Calendar.YEAR) == todayCalendar.get(java.util.Calendar.YEAR)
                },
                "SEBELUMNYA" to previous.filter {
                    val millis = scheduleDateMillis(it.date) ?: Long.MAX_VALUE
                    java.util.Calendar.getInstance().apply { timeInMillis = millis }.get(java.util.Calendar.YEAR) < todayCalendar.get(java.util.Calendar.YEAR)
                }
            )

            groups.forEach { (heading, entries) ->
                if (entries.isNotEmpty()) {
                    item(key = "heading-$heading") { ScheduleSectionHeading(heading) }
                    items(entries, key = { "past-${it.id}" }) { schedule ->
                        ScheduleCard(
                            schedule = schedule,
                            onClick = {
                                if (isSelectionMode) selectedScheduleIds = selectedScheduleIds.toggleId(schedule.id)
                            },
                            onLongPress = { selectedScheduleIds = selectedScheduleIds + schedule.id },
                            onEdit = null,
                            onDelete = {
                                viewModel.deleteSchedule(schedule.id)
                                selectedScheduleIds = selectedScheduleIds - schedule.id
                            },
                            onToggleComplete = null,
                            isPrevious = true,
                            selected = schedule.id in selectedScheduleIds
                        )
                    }
                }
            }

            if (previous.isEmpty()) {
                item {
                    ScheduleEmptyState("Belum ada jadwal sebelumnya", "Aktivitas yang sudah lewat akan tampil di sini.")
                }
            }
        }
    }

    if (showDeleteConfirmation && isSelectionMode) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(localizedUiLabel("Hapus jadwal?")) },
            text = { Text(localizedCount("delete_schedules_count", selectedScheduleIds.size)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSchedules(selectedScheduleIds)
                    selectedScheduleIds = emptySet()
                    showDeleteConfirmation = false
                }) { Text(localizedUiLabel("Hapus")) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) { Text(localizedUiLabel("Batal")) }
            }
        )
    }
}

private fun Set<Int>.toggleId(id: Int): Set<Int> =
    if (id in this) this - id else this + id

@Composable
fun ScheduleDetailScreen(
    navController: NavController,
    scheduleId: Int,
    viewModel: ScheduleViewModel = viewModel()
) {
    val schedules by viewModel.fullSchedules.collectAsState()
    val schedule = schedules.find { it.id == scheduleId }
    Column(modifier = Modifier.fillMaxSize()) {
        StudyMateBackHeader(
            title = "Detail Jadwal",
            onBack = { navController.popBackStack() }
        )

        if (schedule == null) {
            ScheduleEmptyState("Jadwal tidak ditemukan", "Jadwal ini mungkin sudah dihapus.")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(schedule.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(categoryIcon(schedule.subject), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(localizedUiLabel(schedule.subject), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                }
                ScheduleDetailField(
                    if (schedule.subject == "Tugas Sekolah") "Deadline" else "Tanggal",
                    schedule.date
                )
                val timeRange = listOf(schedule.startTime, schedule.endTime)
                    .filter { it.isNotBlank() && !(schedule.subject == "Tugas Sekolah" && it == "00:00") }
                    .joinToString(" – ")
                if (timeRange.isNotBlank()) ScheduleDetailField("Waktu", timeRange)
                ScheduleDetailField(
                    "Catatan",
                    schedule.note.ifBlank { localizedUiLabel("Tidak ada catatan.") }
                )
                if (schedule.teacher.isNotBlank() && schedule.subject != "Tugas Sekolah" && schedule.subject != "Rumah") {
                    ScheduleDetailField("Pengajar", schedule.teacher)
                }
            }
            Text(
                localizedUiLabel(if (schedule.isCompleted) "Status: Selesai" else "Status: Belum selesai"),
                modifier = Modifier.padding(horizontal = 20.dp),
                style = MaterialTheme.typography.titleSmall,
                color = if (schedule.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(
                onClick = { viewModel.setScheduleCompleted(schedule.id, !schedule.isCompleted) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(localizedUiLabel(if (schedule.isCompleted) "Batalkan Selesai" else "Tandai Selesai"))
            }
        }
    }
}

@Composable
private fun ScheduleDetailField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(localizedUiLabel(label), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun dayDifference(todayStart: Long, date: String): Int {
    val dateMillis = scheduleDateMillis(date) ?: return Int.MAX_VALUE
    fun dayOrdinal(timestamp: Long): Long {
        val localDate = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        return java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(
                localDate.get(java.util.Calendar.YEAR),
                localDate.get(java.util.Calendar.MONTH),
                localDate.get(java.util.Calendar.DAY_OF_MONTH)
            )
        }.timeInMillis / (24 * 60 * 60 * 1000L)
    }
    return (dayOrdinal(todayStart) - dayOrdinal(dateMillis)).toInt()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScheduleScreen(navController: NavController, scheduleId: Int? = null, viewModel: ScheduleViewModel = viewModel()) {
    val existing = scheduleId?.let { viewModel.getById(it) }
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var subject by remember { mutableStateOf(existing?.subject ?: "") }
    var date by remember { mutableStateOf(existing?.date ?: "") }
    var start by remember {
        mutableStateOf(existing?.startTime?.takeUnless { existing.subject == "Tugas Sekolah" && it == "00:00" } ?: "")
    }
    var end by remember {
        mutableStateOf(existing?.endTime?.takeUnless { existing.subject == "Tugas Sekolah" && it == "00:00" } ?: "")
    }
    var note by remember { mutableStateOf(existing?.note ?: "") }
    var teacher by remember { mutableStateOf(existing?.teacher ?: "") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var showPastDateWarning by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val language = LocalAppLanguage.current
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.forLanguageTag(language))
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary
    )

    fun saveSchedule() {
        val savedTeacher = if (subject == "Tugas Sekolah" || subject == "Rumah") "" else teacher.trim()
        if (existing == null) {
            viewModel.addSchedule(title.trim(), subject.trim(), date.trim(), start.trim(), end.trim(), note.trim(), savedTeacher)
        } else {
            viewModel.updateSchedule(existing.id, title.trim(), subject.trim(), date.trim(), start.trim(), end.trim(), note.trim(), savedTeacher)
        }
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        StudyMateBackHeader(
            title = if (existing == null) "Tambah Jadwal" else "Edit Jadwal",
            onBack = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ScheduleInputField(
                value = title,
                onValueChange = {
                    title = it
                    validationError = null
                },
                label = "Judul",
                placeholder = "Contoh: Belajar Matematika",
                colors = fieldColors
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                ScheduleInputField(
                    value = subject,
                    displayValue = localizedUiLabel(subject),
                    onValueChange = {},
                    label = "Kategori",
                    placeholder = "Pilih kategori",
                    readOnly = true,
                    colors = fieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    scheduleCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(localizedUiLabel(category)) },
                            onClick = {
                                subject = category
                                if (category == "Tugas Sekolah" || category == "Rumah") teacher = ""
                                categoryExpanded = false
                                validationError = null
                            }
                        )
                    }
                }
            }

            ScheduleInputField(
                value = date,
                onValueChange = {},
                label = if (subject == "Tugas Sekolah") "Deadline" else "Tanggal",
                placeholder = "Pilih tanggal",
                readOnly = true,
                colors = fieldColors,
                trailingIcon = {
                    IconButton(onClick = {
                        val now = java.util.Calendar.getInstance()
                        val picker = android.app.DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val selectedDate = java.util.Calendar.getInstance()
                                selectedDate.set(year, month, day)
                                date = sdf.format(selectedDate.time)
                                validationError = null
                            },
                            now.get(java.util.Calendar.YEAR),
                            now.get(java.util.Calendar.MONTH),
                            now.get(java.util.Calendar.DAY_OF_MONTH)
                        )
                        picker.show()
                    }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = localizedUiLabel("Pilih tanggal"))
                    }
                }
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val startTimeField: @Composable (Modifier) -> Unit = { fieldModifier ->
                    ScheduleInputField(
                        value = start,
                        onValueChange = {},
                        label = if (subject == "Tugas Sekolah") "Mulai (Opsional)" else "Mulai",
                        placeholder = "19:00",
                        readOnly = true,
                        modifier = fieldModifier,
                        colors = fieldColors,
                        trailingIcon = {
                            IconButton(onClick = {
                                val now = java.util.Calendar.getInstance()
                                val picker = android.app.TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        start = String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute)
                                        validationError = null
                                    },
                                    now.get(java.util.Calendar.HOUR_OF_DAY),
                                    now.get(java.util.Calendar.MINUTE),
                                    true
                                )
                                picker.show()
                            }) {
                                Icon(Icons.Default.Schedule, contentDescription = localizedUiLabel("Pilih jam mulai"))
                            }
                        }
                    )
                }
                val endTimeField: @Composable (Modifier) -> Unit = { fieldModifier ->
                    ScheduleInputField(
                        value = end,
                        onValueChange = {},
                        label = if (subject == "Tugas Sekolah") "Selesai (Opsional)" else "Selesai",
                        placeholder = "20:30",
                        readOnly = true,
                        modifier = fieldModifier,
                        colors = fieldColors,
                        trailingIcon = {
                            IconButton(onClick = {
                                val now = java.util.Calendar.getInstance()
                                val picker = android.app.TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        end = String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute)
                                        validationError = null
                                    },
                                    now.get(java.util.Calendar.HOUR_OF_DAY),
                                    now.get(java.util.Calendar.MINUTE),
                                    true
                                )
                                picker.show()
                            }) {
                                Icon(Icons.Default.Schedule, contentDescription = localizedUiLabel("Pilih jam selesai"))
                            }
                        }
                    )
                }

                if (maxWidth < 360.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        startTimeField(Modifier.fillMaxWidth())
                        endTimeField(Modifier.fillMaxWidth())
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        startTimeField(Modifier.weight(1f))
                        endTimeField(Modifier.weight(1f))
                    }
                }
            }

            ScheduleInputField(
                value = note,
                onValueChange = { note = it },
                label = "Catatan",
                placeholder = "Tambahkan deskripsi aktivitas...",
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                minLines = 3,
                singleLine = false
            )
            if (subject != "Tugas Sekolah" && subject != "Rumah") {
                ScheduleInputField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = if (subject == "Belajar" || subject == "Olahraga") "Pengajar (Opsional)" else "Pengajar",
                    placeholder = "Contoh: Pak Budi",
                    colors = fieldColors
                )
            }

            validationError?.let { error ->
                Text(
                    text = localizedUiLabel(error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    validationError = when {
                        title.isBlank() -> "Judul wajib diisi."
                        subject.isBlank() -> "Kategori wajib dipilih."
                        date.isBlank() -> "Tanggal wajib dipilih."
                        subject != "Tugas Sekolah" && start.isBlank() -> "Waktu mulai wajib dipilih."
                        subject != "Tugas Sekolah" && end.isBlank() -> "Waktu selesai wajib dipilih."
                        else -> null
                    }
                    if (validationError == null) {
                        val selectedDay = scheduleDateMillis(date)
                        if (selectedDay != null && selectedDay < startOfTodayMillis()) {
                            showPastDateWarning = true
                        } else {
                            saveSchedule()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(localizedUiLabel("Simpan"), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            }
        }
    }
    if (showPastDateWarning) {
        AlertDialog(
            onDismissRequest = { showPastDateWarning = false },
            title = { Text(localizedUiLabel("Tanggal sudah lewat")) },
            text = {
                Text(localizedUiLabel("Jadwal ini akan disimpan sebagai jadwal lampau dan tidak akan menerima pengingat baru."))
            },
            confirmButton = {
                TextButton(onClick = {
                    showPastDateWarning = false
                    saveSchedule()
                }) { Text(localizedUiLabel("Tetap Simpan")) }
            },
            dismissButton = {
                TextButton(onClick = { showPastDateWarning = false }) { Text(localizedUiLabel("Ubah Tanggal")) }
            }
        )
    }
}

@Composable
private fun ScheduleInputField(
    value: String,
    displayValue: String = value,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    colors: TextFieldColors,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val language = LocalAppLanguage.current
    OutlinedTextField(
        value = displayValue,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(localizedUiLabel(language, label)) },
        placeholder = { Text(localizedUiLabel(language, placeholder), maxLines = 1, overflow = TextOverflow.Ellipsis) },
        readOnly = readOnly,
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(14.dp),
        colors = colors,
        trailingIcon = trailingIcon
    )
}
