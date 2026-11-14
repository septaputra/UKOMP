package com.example.studymate.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studymate.ui.components.StudyMateButton
import com.example.studymate.ui.components.StudyMateTextField
import com.example.studymate.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // We'll call onRegisterSuccess directly after successful register (no auto-auth)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Title
        Text(
            text = "StudyMate",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Name field
        StudyMateTextField(
            value = name,
            onValueChange = { name = it },
            label = "Nama Lengkap",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Email field
        StudyMateTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Password field
        StudyMateTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Confirm Password field
        StudyMateTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Konfirmasi Password",
            isPassword = true,
            isError = password != confirmPassword && confirmPassword.isNotBlank(),
            errorMessage = if (password != confirmPassword && confirmPassword.isNotBlank()) "Password tidak cocok" else null,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Register button
        StudyMateButton(
            text = if (isLoading) "Mendaftarkan..." else "Register",
            onClick = {
                isLoading = true
                val success = authViewModel.register(name, email, password, confirmPassword)
                isLoading = false
                if (success) {
                    // After successful register, navigate back to login
                    onRegisterSuccess()
                } else {
                    // Show error (for now just reset loading)
                }
            },
            enabled = !isLoading &&
                     name.isNotBlank() &&
                     email.isNotBlank() &&
                     password.isNotBlank() &&
                     confirmPassword.isNotBlank() &&
                     password == confirmPassword,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Login link
        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sudah punya akun? ",
                color = Color(0xFF1A2238)
            )
            TextButton(
                onClick = onNavigateToLogin,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Masuk",
                    color = Color(0xFFFF6A3D),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}