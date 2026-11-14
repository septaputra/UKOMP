package com.example.studymate.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studymate.ui.localization.appString
import com.example.studymate.viewmodel.AppPreferencesViewModel
import com.example.studymate.ui.components.StudyMateBackHeader

private val supportedLanguages = listOf(
    "id" to "Bahasa Indonesia",
    "en" to "English",
    "es" to "Español",
    "fr" to "Français",
    "de" to "Deutsch",
    "pt" to "Português",
    "ja" to "日本語",
    "ko" to "한국어"
)

@Composable
fun AppSettingsScreen(
    navController: NavController,
    preferencesViewModel: AppPreferencesViewModel
) {
    val preferences by preferencesViewModel.preferencesState.collectAsState()
    var showLanguages by remember { mutableStateOf(false) }
    val selectedLanguage = supportedLanguages.firstOrNull { it.first == preferences.languageTag }?.second
        ?: supportedLanguages.first().second

    Column(modifier = Modifier.fillMaxSize()) {
        StudyMateBackHeader(
            title = appString(preferences.languageTag, "app_settings"),
            onBack = { navController.popBackStack() }
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsSection(
                    title = appString(preferences.languageTag, "language"),
                    description = appString(preferences.languageTag, "language_description"),
                    value = selectedLanguage,
                    onClick = { showLanguages = true }
                )
            }
            item {
                Text(appString(preferences.languageTag, "theme"), style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        appString(
                            preferences.languageTag,
                            if (preferences.darkMode) "dark_mode" else "light_mode"
                        )
                    )
                    Switch(
                        checked = !preferences.darkMode,
                        onCheckedChange = { isLight -> preferencesViewModel.setDarkMode(!isLight) }
                    )
                }
            }
        }
    }

    if (showLanguages) {
        AlertDialog(
            onDismissRequest = { showLanguages = false },
            title = { Text(appString(preferences.languageTag, "language")) },
            text = {
                Column {
                    supportedLanguages.forEach { (tag, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                preferencesViewModel.setLanguage(tag)
                                showLanguages = false
                            }.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label)
                            if (tag == preferences.languageTag) Text("✓", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    description: String,
    value: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
    }
}
