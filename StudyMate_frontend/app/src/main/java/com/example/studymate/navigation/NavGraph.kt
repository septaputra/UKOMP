package com.example.studymate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studymate.ui.screens.auth.LoginScreen
import com.example.studymate.ui.screens.auth.RegisterScreen
import com.example.studymate.ui.screens.main.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Main : Screen("main")
    object Dashboard : Screen("dashboard")
    object Schedule : Screen("schedule")
    object Notes : Screen("notes")
    object Tasks : Screen("tasks")
    object Pomodoro : Screen("pomodoro")
    object User : Screen("user")
    object Settings : Screen("settings")
}

@Composable
fun StudyMateNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                // Navigate to forgot password
                onNavigateToForgot = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                // After successful login navigate explicitly to the Main (app shell)
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                // After successful register navigate back to Login (do not auto-login)
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            com.example.studymate.ui.screens.auth.ForgotPasswordScreen(navController = navController)
        }

        // Main app shell route. This composable renders the `MainScreen`
        // which contains the Scaffold and the NavHost for the bottom tabs.
        composable(Screen.Main.route) {
            MainScreen(navController)
        }
    }
}