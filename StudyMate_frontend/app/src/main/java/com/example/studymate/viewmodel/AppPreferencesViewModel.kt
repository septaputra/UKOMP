package com.example.studymate.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.studymate.ui.localization.appLanguageForTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppPreferences(
    val languageTag: String = "id",
    val darkMode: Boolean = false
)

class AppPreferencesViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private val savedLanguage = appLanguageForTag(preferences.getString("language_tag", "id") ?: "id")
    private val _preferences = MutableStateFlow(
        AppPreferences(
            languageTag = savedLanguage,
            darkMode = preferences.getBoolean("dark_mode", false)
        )
    )
    val preferencesState: StateFlow<AppPreferences> = _preferences.asStateFlow()

    init {
        if (preferences.getString("language_tag", "id") != savedLanguage) {
            preferences.edit().putString("language_tag", savedLanguage).apply()
        }
    }

    fun setLanguage(languageTag: String) {
        val supportedTag = appLanguageForTag(languageTag)
        preferences.edit().putString("language_tag", supportedTag).apply()
        _preferences.value = _preferences.value.copy(languageTag = supportedTag)
    }

    fun setDarkMode(enabled: Boolean) {
        preferences.edit().putBoolean("dark_mode", enabled).apply()
        _preferences.value = _preferences.value.copy(darkMode = enabled)
    }
}
