package com.example.studymate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.studymate.navigation.StudyMateNavGraph
import com.example.studymate.navigation.Screen
import com.example.studymate.ui.screens.main.MainScreen
import com.example.studymate.ui.theme.StudyMateTheme
import com.example.studymate.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyMateTheme {
                val authViewModel: AuthViewModel = viewModel()

                // Observe auth state to decide which navigation to show
                val authState = authViewModel.authState.value

                // Always host a single root NavHost. Start destination depends on auth state.
                val navController = rememberNavController()
                val start = if (authState is com.example.studymate.viewmodel.AuthState.Authenticated) {
                    Screen.Main.route
                } else {
                    Screen.Login.route
                }

                StudyMateNavGraph(
                    navController = navController,
                    startDestination = start
                )
            }
        }
    }
}