package com.example.studymate.viewmodel

import androidx.lifecycle.ViewModel
import com.example.studymate.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.NotAuthenticated)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // Dummy authentication
    fun login(email: String, password: String): Boolean {
        // Simple dummy check
        if (email.isNotBlank() && password.isNotBlank()) {
            _currentUser.value = User.dummy.copy(email = email)
            _authState.value = AuthState.Authenticated
            return true
        }
        return false
    }

    fun register(name: String, email: String, password: String, confirmPassword: String): Boolean {
        // Simple dummy check
        if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && password == confirmPassword) {
            // For now do not auto-authenticate on register. Return success and let UI navigate back to Login.
            return true
        }
        return false
    }

    fun updateProfileName(name: String): Boolean {
        val user = _currentUser.value ?: return false
        val normalizedName = name.trim()
        if (normalizedName.isBlank()) return false
        _currentUser.value = user.copy(name = normalizedName)
        return true
    }

    fun updateProfileImage(uri: String?): Boolean {
        val user = _currentUser.value ?: return false
        _currentUser.value = user.copy(profileImageUri = uri)
        return true
    }

    fun updateProfile(
        name: String,
        fullName: String,
        bio: String,
        educationLevel: String,
        school: String,
        gradeOrSemester: String,
        imageUri: String?
    ): Boolean {
        val user = _currentUser.value ?: return false
        val normalizedName = name.trim()
        if (normalizedName.isBlank() || normalizedName.length > 80) return false
        _currentUser.value = user.copy(
            name = normalizedName,
            fullName = fullName.trim(),
            bio = bio.trim(),
            educationLevel = educationLevel.trim(),
            school = school.trim(),
            gradeOrSemester = gradeOrSemester.trim(),
            profileImageUri = imageUri
        )
        return true
    }

    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.NotAuthenticated
    }
}

sealed class AuthState {
    object NotAuthenticated : AuthState()
    object Authenticated : AuthState()
}