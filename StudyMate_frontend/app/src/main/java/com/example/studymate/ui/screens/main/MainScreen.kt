package com.example.studymate.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studymate.navigation.Screen

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard.route, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.Schedule.route, "Schedule", Icons.Filled.DateRange),
    BottomNavItem(Screen.Notes.route, "Notes", Icons.Filled.Edit),
    BottomNavItem(Screen.Pomodoro.route, "Focus", Icons.Filled.PlayArrow),
    BottomNavItem(Screen.User.route, "User", Icons.Filled.Person)
)

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
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
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Schedule.route) { com.example.studymate.ui.screens.schedule.ScheduleScreen(navController) }
            composable(Screen.Notes.route) { com.example.studymate.ui.screens.notes.NotesScreen(navController) }
            // Notes sub-routes (use same NavController; no nested NavHost)
            composable("add_note") {
                com.example.studymate.ui.screens.notes.AddEditNoteScreen(navController = navController)
            }
            composable(
                "edit_note/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id")
                com.example.studymate.ui.screens.notes.AddEditNoteScreen(navController = navController, noteId = id)
            }
            // Schedule sub-routes
            composable("add_schedule") {
                com.example.studymate.ui.screens.schedule.AddEditScheduleScreen(navController = navController)
            }
            composable(
                "edit_schedule/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id")
                com.example.studymate.ui.screens.schedule.AddEditScheduleScreen(navController = navController, scheduleId = id)
            }
            composable(Screen.Pomodoro.route) { PomodoroScreen() }
            composable(Screen.User.route) {
                UserScreen(navController = rootNavController)
            }
        }
    }
}