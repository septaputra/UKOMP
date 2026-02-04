package com.example.studymate.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val StudyMateColorScheme = lightColorScheme(
    primary = StudyMateCTA,
    onPrimary = StudyMateBackground,
    secondary = StudyMateSecondaryText,
    onSecondary = StudyMateBackground,
    tertiary = StudyMateNavigation,
    background = StudyMateBackground,
    onBackground = StudyMatePrimaryText,
    surface = StudyMateBackground,
    onSurface = StudyMatePrimaryText,
    surfaceVariant = StudyMateCardBackground,
    onSurfaceVariant = StudyMateSecondaryText
)

@Composable
fun StudyMateTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StudyMateColorScheme,
        typography = Typography,
        content = content
    )
}