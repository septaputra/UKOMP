package com.example.studymate.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studymate.navigation.Screen
import com.example.studymate.ui.screens.schedule.ScheduleViewModel
import com.example.studymate.ui.screens.schedule.ScheduleNotificationNavigation
import com.example.studymate.ui.screens.notes.NotesViewModel
import com.example.studymate.viewmodel.AuthViewModel
import com.example.studymate.viewmodel.DashboardViewModel
import com.example.studymate.viewmodel.AppPreferencesViewModel
import com.example.studymate.ui.localization.appString
import com.example.studymate.ui.localization.LocalAppLanguage

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun MainScreen(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel,
    preferencesViewModel: AppPreferencesViewModel
) {
    val navController = rememberNavController()
    val requestedScheduleId by ScheduleNotificationNavigation.scheduleId.collectAsState()
    val scheduleViewModel: ScheduleViewModel = viewModel()
    val notesViewModel: NotesViewModel = viewModel()
    val dashboardViewModel: DashboardViewModel = viewModel()
    val focusTimerViewModel: FocusTimerViewModel = viewModel()
    val learningGoalsViewModel: LearningGoalsViewModel = viewModel()
    val language = preferencesViewModel.preferencesState.collectAsState().value.languageTag
    val bottomNavItems = listOf(
        BottomNavItem(Screen.Dashboard.route, appString(language, "home"), Icons.Filled.Home),
        BottomNavItem(Screen.Schedule.route, appString(language, "schedule"), Icons.Filled.DateRange),
        BottomNavItem(Screen.Notes.route, appString(language, "notes"), Icons.Filled.Edit),
        BottomNavItem(Screen.Pomodoro.route, appString(language, "focus"), Icons.Filled.PlayArrow),
        BottomNavItem(Screen.User.route, appString(language, "user"), Icons.Filled.Person)
    )

    LaunchedEffect(requestedScheduleId) {
        requestedScheduleId?.let { scheduleId ->
            navController.navigate(Screen.Schedule.route) { launchSingleTop = true }
            navController.navigate("schedule_detail/$scheduleId") { launchSingleTop = true }
            ScheduleNotificationNavigation.consume()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val activeParentRoute = parentRouteFor(currentDestination?.route)

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = activeParentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = Color(0xFF9E9E9E),
                            unselectedTextColor = Color(0xFF9E9E9E),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    )
                }
            }

        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalAppLanguage provides language) {
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
            ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    navController,
                    dashboardViewModel,
                    scheduleViewModel,
                    notesViewModel,
                    focusTimerViewModel
                )
            }
            composable(Screen.Schedule.route) {
                com.example.studymate.ui.screens.schedule.ScheduleScreen(navController, scheduleViewModel)
            }
            composable(Screen.Notes.route) {
                com.example.studymate.ui.screens.notes.NotesScreen(navController, notesViewModel)
            }
            // Notes sub-routes (use same NavController; no nested NavHost)
            composable("add_note") {
                com.example.studymate.ui.screens.notes.AddEditNoteScreen(
                    navController = navController,
                    viewModel = notesViewModel
                )
            }
            composable(
                "edit_note/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id")
                com.example.studymate.ui.screens.notes.AddEditNoteScreen(
                    navController = navController,
                    noteId = id,
                    viewModel = notesViewModel
                )
            }
            // Schedule sub-routes
            composable("add_schedule") {
                com.example.studymate.ui.screens.schedule.AddEditScheduleScreen(
                    navController = navController,
                    viewModel = scheduleViewModel
                )
            }
            composable(
                "edit_schedule/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id")
                com.example.studymate.ui.screens.schedule.AddEditScheduleScreen(
                    navController = navController,
                    scheduleId = id,
                    viewModel = scheduleViewModel
                )
            }
            composable("previous_schedules") {
                com.example.studymate.ui.screens.schedule.PreviousSchedulesScreen(
                    navController = navController,
                    viewModel = scheduleViewModel
                )
            }
            composable(
                "schedule_detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                com.example.studymate.ui.screens.schedule.ScheduleDetailScreen(
                    navController = navController,
                    scheduleId = id,
                    viewModel = scheduleViewModel
                )
            }
            composable(Screen.Pomodoro.route) { PomodoroScreen(focusTimerViewModel) }
            composable("notifications") {
                NotificationsScreen(navController, dashboardViewModel)
            }
            composable(
                "notification_detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                NotificationDetailScreen(navController, dashboardViewModel, id)
            }
            composable("progress") {
                LearningProgressScreen(
                    navController,
                    scheduleViewModel,
                    notesViewModel,
                    focusTimerViewModel,
                    learningGoalsViewModel
                )
            }
            composable("add_learning_goal") {
                AddLearningGoalScreen(navController, learningGoalsViewModel)
            }
            composable(
                "edit_learning_goal/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                AddLearningGoalScreen(navController, learningGoalsViewModel, id)
            }
            composable("tutoring_list") {
                TutoringCentersScreen(navController, dashboardViewModel)
            }
            composable(
                "tutoring_detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                TutoringCenterDetailScreen(navController, dashboardViewModel, id)
            }
            composable("learning_videos") {
                LearningVideosScreen(navController, dashboardViewModel)
            }
            composable(
                "video_detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                LearningVideoDetailScreen(navController, dashboardViewModel, id)
            }
            composable("news_list") {
                NewsListScreen(navController, dashboardViewModel)
            }
            composable(
                "news_detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                NewsDetailScreen(navController, dashboardViewModel, id)
            }
            composable(Screen.User.route) {
                UserScreen(
                    navController = navController,
                    rootNavController = rootNavController,
                    authViewModel = authViewModel,
                    preferencesViewModel = preferencesViewModel
                )
            }
            composable("user_app_settings") {
                AppSettingsScreen(
                    navController = navController,
                    preferencesViewModel = preferencesViewModel
                )
            }
            composable("user_edit_profile") {
                EditProfileScreen(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
            composable("user_account_security") {
                AccountSecurityScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    learningGoalsViewModel = learningGoalsViewModel
                )
            }
            composable("user_help") {
                HelpCenterScreen(navController = navController)
            }
            composable("user_about") {
                AboutStudyMateScreen(navController = navController)
                }
            }
        }
    }
}

private fun parentRouteFor(route: String?): String? = when (route) {
    Screen.Dashboard.route,
    "notifications",
    "notification_detail/{id}",
    "progress",
    "add_learning_goal",
    "edit_learning_goal/{id}",
    "tutoring_list",
    "tutoring_detail/{id}",
    "learning_videos",
    "video_detail/{id}",
    "news_list",
    "news_detail/{id}" -> Screen.Dashboard.route

    Screen.Schedule.route,
    "add_schedule",
    "edit_schedule/{id}",
    "previous_schedules",
    "schedule_detail/{id}" -> Screen.Schedule.route

    Screen.Notes.route,
    "add_note",
    "edit_note/{id}" -> Screen.Notes.route

    Screen.Pomodoro.route -> Screen.Pomodoro.route
    Screen.User.route,
    "user_edit_profile",
    "user_account_security",
    "user_help",
    "user_about",
    "user_app_settings" -> Screen.User.route
    else -> null
}