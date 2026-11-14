package com.example.studymate

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.studymate.navigation.StudyMateNavGraph
import com.example.studymate.navigation.Screen
import com.example.studymate.ui.screens.schedule.ScheduleNotificationNavigation
import com.example.studymate.ui.theme.StudyMateTheme
import com.example.studymate.viewmodel.AuthViewModel
import com.example.studymate.viewmodel.AppPreferencesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        val splashStartedAt = SystemClock.uptimeMillis()
        splashScreen.setKeepOnScreenCondition {
            SystemClock.uptimeMillis() - splashStartedAt < SPLASH_DURATION_MILLIS
        }
        super.onCreate(savedInstanceState)
        publishScheduleIntent(intent)
        enableEdgeToEdge()
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val preferencesViewModel: AppPreferencesViewModel = viewModel()
            val preferences by preferencesViewModel.preferencesState.collectAsState()
            StudyMateTheme(darkTheme = preferences.darkMode) {
                val authState by authViewModel.authState.collectAsState()
                val navController = rememberNavController()
                val start = if (authState is com.example.studymate.viewmodel.AuthState.Authenticated) {
                    Screen.Main.route
                } else {
                    Screen.Login.route
                }
                StudyMateNavGraph(
                    navController = navController,
                    startDestination = start,
                    authViewModel = authViewModel,
                    preferencesViewModel = preferencesViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        publishScheduleIntent(intent)
    }

    private fun publishScheduleIntent(intent: android.content.Intent?) {
        val scheduleId = intent?.getIntExtra(ScheduleNotificationNavigation.EXTRA_SCHEDULE_ID, -1) ?: -1
        if (scheduleId > 0) ScheduleNotificationNavigation.publish(scheduleId)
    }

    private companion object {
        const val SPLASH_DURATION_MILLIS = 3_500L
    }
}