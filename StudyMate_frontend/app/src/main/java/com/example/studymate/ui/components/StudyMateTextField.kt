package com.example.studymate.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun StudyMateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        isError = isError,
        supportingText = errorMessage?.let { { Text(it) } },
        trailingIcon = {
            if (isPassword) {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    androidx.compose.material3.Text(
                        text = if (passwordVisible) "Sembunyikan" else "Tampilkan",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFFFFFFF),
            unfocusedContainerColor = Color(0xFFFFFFFF),
            disabledContainerColor = Color(0xFFFFFFFF),
            errorContainerColor = Color(0xFFFFFFFF),
            focusedBorderColor = Color(0xFFFF6A3D),
            unfocusedBorderColor = Color(0xFF9DAAF2),
            disabledBorderColor = Color(0xFF9DAAF2).copy(alpha = 0.5f),
            errorBorderColor = Color(0xFFE53935),
            focusedLabelColor = Color(0xFFFF6A3D),
            unfocusedLabelColor = Color(0xFF9E9E9E),
            disabledLabelColor = Color(0xFF9E9E9E).copy(alpha = 0.5f),
            errorLabelColor = Color(0xFFE53935),
            cursorColor = Color(0xFFFF6A3D),
            focusedTextColor = Color(0xFF1A2238),
            unfocusedTextColor = Color(0xFF1A2238),
            disabledTextColor = Color(0xFF1A2238).copy(alpha = 0.5f),
            errorTextColor = Color(0xFF1A2238)
        ),
        shape = MaterialTheme.shapes.medium
    )
}