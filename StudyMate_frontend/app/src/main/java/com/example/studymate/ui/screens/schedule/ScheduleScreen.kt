package com.example.studymate.ui.screens.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.studymate.ui.components.buildHighlightedText
import com.example.studymate.ui.components.SearchBar
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleScreen(navController: NavController, viewModel: ScheduleViewModel = viewModel()) {
    val schedules by viewModel.filteredSchedules.collectAsState()
    val query by viewModel.query.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(query = query, onQueryChange = { q -> viewModel.setQuery(q) }, placeholder = "Cari jadwal...")
            Spacer(modifier = Modifier.height(8.dp))
            ScheduleListScreen(schedules = schedules, onEdit = { id -> navController.navigate("edit_schedule/$id") }, onDelete = { id -> viewModel.deleteSchedule(id) }, query = query)
        }

        FloatingActionButton(
            onClick = { navController.navigate("add_schedule") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 24.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Jadwal")
        }
    }
}

@Composable
fun ScheduleListScreen(schedules: List<Schedule>, onEdit: (Int) -> Unit, onDelete: (Int) -> Unit, query: String = "") {
    val grouped = schedules.groupBy { it.date }.toSortedMap()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        grouped.forEach { (date, items) ->
            item {
                Text(text = date, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), modifier = Modifier.padding(vertical = 4.dp))
            }
            items(items) { s ->
                ScheduleCard(schedule = s, onClick = { onEdit(s.id) }, onLongPress = { onDelete(s.id) }, query = query)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleCard(schedule: Schedule, onClick: () -> Unit, onLongPress: () -> Unit, query: String = "") {
    Card(modifier = Modifier
        .fillMaxWidth()
        .combinedClickable(onClick = onClick, onLongClick = onLongPress)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = buildHighlightedText(schedule.title, query), style = MaterialTheme.typography.titleMedium)
                    Text(text = buildHighlightedText(schedule.subject, query), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                Text(text = "${schedule.startTime} - ${schedule.endTime}", style = MaterialTheme.typography.bodySmall)
            }

            if (schedule.note.isNotBlank()) {
                Text(text = buildHighlightedText(schedule.note, query), style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun AddEditScheduleScreen(navController: NavController, scheduleId: Int? = null, viewModel: ScheduleViewModel = viewModel()) {
    val existing = scheduleId?.let { viewModel.getById(it) }
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var subject by remember { mutableStateOf(existing?.subject ?: "") }
    var date by remember { mutableStateOf(existing?.date ?: "") }
    var start by remember { mutableStateOf(existing?.startTime ?: "") }
    var end by remember { mutableStateOf(existing?.endTime ?: "") }
    var note by remember { mutableStateOf(existing?.note ?: "") }
    var teacher by remember { mutableStateOf(existing?.teacher ?: "") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.Top) {
        Text(text = if (existing == null) "Tambah Jadwal" else "Edit Jadwal", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp))
        OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Mata Pelajaran") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        // Date & Time pickers: fields are read-only and open native pickers
        val context = androidx.compose.ui.platform.LocalContext.current
        val cal = java.util.Calendar.getInstance()
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())

        // Date picker
        OutlinedTextField(
            value = date,
            onValueChange = { /* read-only */ },
            label = { Text("Tanggal (contoh: 28 Jan 2026)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            readOnly = true,
            trailingIcon = {
                androidx.compose.material3.IconButton(onClick = {
                    val now = java.util.Calendar.getInstance()
                    val picker = android.app.DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            val c = java.util.Calendar.getInstance()
                            c.set(y, m, d)
                            date = sdf.format(c.time)
                        },
                        now.get(java.util.Calendar.YEAR),
                        now.get(java.util.Calendar.MONTH),
                        now.get(java.util.Calendar.DAY_OF_MONTH)
                    )
                    picker.show()
                }) {
                    Icon(imageVector = androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Pilih tanggal")
                }
            }
        )

        // Time pickers for start and end
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = start,
                onValueChange = { /* read-only */ },
                label = { Text("Mulai (19:00)") },
                modifier = Modifier.weight(1f).padding(top = 8.dp),
                readOnly = true,
                trailingIcon = {
                    androidx.compose.material3.IconButton(onClick = {
                        val now = java.util.Calendar.getInstance()
                        val t = android.app.TimePickerDialog(context, { _, hour, minute ->
                            start = String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute)
                        }, now.get(java.util.Calendar.HOUR_OF_DAY), now.get(java.util.Calendar.MINUTE), true)
                        t.show()
                    }) {
                        Icon(imageVector = androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Pilih jam mulai")
                    }
                }
            )

            OutlinedTextField(
                value = end,
                onValueChange = { /* read-only */ },
                label = { Text("Selesai (20:30)") },
                modifier = Modifier.weight(1f).padding(top = 8.dp),
                readOnly = true,
                trailingIcon = {
                    androidx.compose.material3.IconButton(onClick = {
                        val now = java.util.Calendar.getInstance()
                        val t = android.app.TimePickerDialog(context, { _, hour, minute ->
                            end = String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute)
                        }, now.get(java.util.Calendar.HOUR_OF_DAY), now.get(java.util.Calendar.MINUTE), true)
                        t.show()
                    }) {
                        Icon(imageVector = androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Pilih jam selesai")
                    }
                }
            )
        }
        OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Catatan") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), maxLines = 4)
        OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Pengajar") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))

        Button(onClick = {
            if (existing == null) {
                viewModel.addSchedule(title.trim(), subject.trim(), date.trim(), start.trim(), end.trim(), note.trim(), teacher.trim())
            } else {
                viewModel.updateSchedule(existing.id, title.trim(), subject.trim(), date.trim(), start.trim(), end.trim(), note.trim(), teacher.trim())
            }
            navController.popBackStack()
        }, modifier = Modifier.padding(top = 12.dp)) {
            Text("Simpan")
        }
    }
}
