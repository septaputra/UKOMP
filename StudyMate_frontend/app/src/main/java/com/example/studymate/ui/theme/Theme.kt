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
import androidx.compose.ui.graphics.Color

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
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme(
            primary = StudyMateCTA,
            onPrimary = StudyMateBackground,
            secondary = StudyMateSecondaryText,
            onSecondary = StudyMateBackground,
            background = Color(0xFF121212),
            onBackground = Color(0xFFF5F5F5),
            surface = Color(0xFF121212),
            onSurface = Color(0xFFF5F5F5),
            surfaceVariant = Color(0xFF2A2A2A),
            onSurfaceVariant = Color(0xFFD0D0D0)
        ) else StudyMateColorScheme,
        typography = Typography,
        content = content
    )
}