package com.example.studymate.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studymate.ui.components.StudyMateButton
import com.example.studymate.ui.components.StudyMateTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController, authViewModel: com.example.studymate.viewmodel.AuthViewModel = viewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Lupa Password", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(inner), horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Masukkan email yang terdaftar. Kami akan mengirimkan instruksi untuk reset password.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            StudyMateTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            StudyMateButton(text = "Kirim Link Reset", onClick = {
                isLoading = true
                // validation
                val valid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                isLoading = false
                if (email.isBlank()) {
                    Toast.makeText(context, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@StudyMateButton
                }
                if (!valid) {
                    Toast.makeText(context, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                    return@StudyMateButton
                }

                // Simulate sending
                Toast.makeText(context, "Link reset password telah dikirim ke email Anda (simulasi)", Toast.LENGTH_LONG).show()
                navController.navigateUp()
            })
        }
    }
}
