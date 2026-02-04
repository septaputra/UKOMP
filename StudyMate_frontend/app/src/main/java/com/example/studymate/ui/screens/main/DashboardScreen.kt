package com.example.studymate.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studymate.viewmodel.AuthViewModel
import com.example.studymate.viewmodel.DashboardViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material3.Icon

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel = viewModel(),
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val dashboardData by dashboardViewModel.dashboardData.collectAsState()

    // Calculate safe top padding to avoid status bar/notch overlap
    val topInsets = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val topPadding = topInsets + 16.dp

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = topPadding, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Greeting
            Text(
                text = "Halo, ${currentUser?.name ?: "Pelajar"}!",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF1A1A2E), // Dark primary text
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Siap belajar hari ini?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4A4A68), // Dark secondary text
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Summary Cards (spacing 12-16dp)
        item {
            SummaryCard(
                title = "Jadwal Hari Ini",
                value = dashboardData.scheduleCount.toString(),
                icon = Icons.Filled.DateRange,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        item {
            SummaryCard(
                title = "Tugas Belum Selesai",
                value = dashboardData.unfinishedTasksCount.toString(),
                icon = Icons.Filled.CheckCircle,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        item {
            SummaryCard(
                title = "Total Catatan",
                value = dashboardData.notesCount.toString(),
                icon = Icons.Filled.Edit,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Progress Section - Option A: Ringkasan aktivitas belajar hari ini
        item {
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                text = "Ringkasan aktivitas belajar hari ini",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LinearProgressIndicator(
                progress = dashboardData.progressPercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = "Kamu telah menyelesaikan ${(dashboardData.progressPercentage * 100).toInt()}% target belajar hari ini",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = value,
                    style = MaterialTheme.typography.displayMedium,
                    color = Color(0xFF3F51B5)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4A4A68), // Dark secondary text for better contrast
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}