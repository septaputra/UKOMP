package com.example.studymate.ui.screens.main

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.studymate.ui.components.StudyMateBackHeader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.studymate.ui.localization.LocalAppLanguage
import com.example.studymate.ui.localization.localizedUiLabel

@Composable
internal fun AddLearningGoalScreen(
    navController: NavHostController,
    goalsViewModel: LearningGoalsViewModel,
    goalId: String? = null
) {
    val existingGoal = remember(goalId) { goalId?.let(goalsViewModel::getGoal) }
    LaunchedEffect(goalId, existingGoal) {
        if (goalId != null && existingGoal == null) navController.popBackStack()
    }
    var name by remember(goalId) { mutableStateOf(existingGoal?.name.orEmpty()) }
    var selectedType by remember(goalId) { mutableStateOf(existingGoal?.type ?: LearningGoalType.LEARNING_ACTIVITIES) }
    var target by remember(goalId) { mutableStateOf(existingGoal?.targetValue?.toString().orEmpty()) }
    var description by remember(goalId) { mutableStateOf(existingGoal?.description.orEmpty()) }
    var deadlineMillis by remember(goalId) { mutableStateOf(existingGoal?.deadlineMillis) }
    var typeExpanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isSaving by remember(goalId) { mutableStateOf(false) }
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    val dateFormat = remember(language) { SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag(language)) }

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        StudyMateBackHeader(
            title = localizedUiLabel(if (existingGoal == null) "Tambah Target" else "Edit Target"),
            onBack = { navController.popBackStack() }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                localizedUiLabel("Rencanakan target belajar yang ingin kamu capai."),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.take(80); error = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(localizedUiLabel("Nama target *")) },
                placeholder = { Text(localizedUiLabel("Menyelesaikan tugas sekolah")) },
                singleLine = true
            )
            Column {
                OutlinedTextField(
                    value = localizedUiLabel(selectedType.label),
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(localizedUiLabel("Jenis target *")) },
                    readOnly = true,
                    trailingIcon = {
                        TextButton(onClick = { typeExpanded = true }) { Text(localizedUiLabel("Pilih")) }
                    }
                )
                DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                    LearningGoalType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(localizedUiLabel(type.label)) },
                            onClick = {
                                selectedType = type
                                typeExpanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = target,
                onValueChange = { value ->
                    if (value.length <= 6 && value.all(Char::isDigit)) {
                        target = value
                        error = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("${localizedUiLabel("Nilai target")} * (${localizedUiLabel(selectedType.unit)})") },
                placeholder = { Text(localizedUiLabel("Masukkan angka lebih dari 0")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                supportingText = { Text("${localizedUiLabel("Satuan:")} ${localizedUiLabel(selectedType.unit)}") }
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it.take(240) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(localizedUiLabel("Deskripsi (opsional)")) },
                placeholder = { Text(localizedUiLabel("Tambahkan keterangan target")) },
                minLines = 3,
                maxLines = 4
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(localizedUiLabel("Tenggat (opsional)"), style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        val now = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                deadlineMillis = Calendar.getInstance().apply {
                                    set(year, month, day, 23, 59, 59)
                                    set(Calendar.MILLISECOND, 999)
                                }.timeInMillis
                            },
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        androidx.compose.material3.Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        Text(
                            deadlineMillis?.let { dateFormat.format(Date(it)) } ?: localizedUiLabel("Pilih tanggal"),
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                    if (deadlineMillis != null) {
                        TextButton(onClick = { deadlineMillis = null }) { Text(localizedUiLabel("Hapus")) }
                    }
                }
            }
            error?.let { Text(localizedUiLabel(it), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(8.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (existingGoal == null) {
                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text(localizedUiLabel("Batal")) }
            }
            Button(
                enabled = !isSaving,
                onClick = {
                    val targetValue = target.toIntOrNull()
                    when {
                        name.isBlank() -> error = "Nama target wajib diisi."
                        targetValue == null || targetValue <= 0 -> error = "Nilai target harus lebih besar dari 0."
                        else -> {
                            isSaving = true
                            if (existingGoal == null) {
                                goalsViewModel.addGoal(name, selectedType, targetValue, deadlineMillis, description)
                            } else {
                                goalsViewModel.updateGoal(existingGoal.id, name, selectedType, targetValue, deadlineMillis, description)
                            }
                            navController.popBackStack()
                        }
                    }
                },
                modifier = (if (existingGoal == null) Modifier.weight(1f) else Modifier.fillMaxWidth())
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    localizedUiLabel(if (existingGoal == null) "Simpan Target" else "Simpan Perubahan"),
                    maxLines = 2,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }

}
