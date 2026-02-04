package com.example.studymate.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val joinDate: String = "22 Januari 2026"
) {
    companion object {
        // Dummy user for testing
        val dummy = User(
            id = "1",
            name = "Putra",
            email = "putra@example.com",
            joinDate = "22 Januari 2026"
        )
    }
}