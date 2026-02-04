package com.example.studymate.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studymate.ui.components.StudyMateButton
import kotlinx.coroutines.delay

@Composable
fun PomodoroScreen() {
    var timeLeft by remember { mutableStateOf(25 * 60) } // 25 minutes in seconds
    var isRunning by remember { mutableStateOf(false) }
    var isBreak by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        if (timeLeft == 0) {
            isRunning = false
            isBreak = !isBreak
            timeLeft = if (isBreak) 5 * 60 else 25 * 60 // 5 min break or 25 min work
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = if (isBreak) "Waktu Istirahat" else "Fokus Belajar",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Timer Display
        Surface(
            modifier = Modifier
                .size(200.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = formatTime(timeLeft),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Progress Indicator
        CircularProgressIndicator(
            progress = { 1f - (timeLeft.toFloat() / (if (isBreak) 5 * 60 else 25 * 60)) },
            modifier = Modifier.size(120.dp),
            color = MaterialTheme.colorScheme.tertiary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Control Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StudyMateButton(
                text = if (isRunning) "Pause" else "Start",
                onClick = { isRunning = !isRunning },
                modifier = Modifier.weight(1f)
            )

            OutlinedButton(
                onClick = {
                    isRunning = false
                    isBreak = false
                    timeLeft = 25 * 60
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Session Info
        Text(
            text = "Sesi ${if (isBreak) "Istirahat" else "Belajar"}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}